package br.nom.rccrv.code.infrastructure.saga;

import br.nom.rccrv.code.api.client.VeiculosRestClient;
import br.nom.rccrv.code.api.kafka.FinanceiroEmitter;
import br.nom.rccrv.code.domain.dto.ack.AckInterface;
import br.nom.rccrv.code.domain.dto.ack.SagaAck;
import br.nom.rccrv.code.domain.enums.StepStatus;
import br.nom.rccrv.code.domain.state.PrincipalSaleContext;
import br.nom.rccrv.code.domain.state.SagaState;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SagaOrchestrator {

  private enum FailingService {
    FINANCEIRO,
    PRINCIPAL
  }

  private static final int SAGA_TIMEOUT_SECONDS = 30;

  private final Logger logger;
  private final ConcurrentHashMap<UUID, SagaState> activeSagas;
  private final ConcurrentHashMap<UUID, PrincipalSaleContext> principalSales;
  private final ScheduledExecutorService timeoutExecutor;
  private final FinanceiroEmitter financeiroEmitter;
  private final VeiculosRestClient veiculosClient;

  @Inject
  public SagaOrchestrator(
      FinanceiroEmitter financeiroEmitter,
      @RestClient VeiculosRestClient veiculosClient) {
    this.financeiroEmitter = financeiroEmitter;
    this.veiculosClient = veiculosClient;
    activeSagas = new ConcurrentHashMap<>();
    principalSales = new ConcurrentHashMap<>();
    logger = LoggerFactory.getLogger(SagaOrchestrator.class);
    timeoutExecutor = Executors.newScheduledThreadPool(1);
  }

  public void registerSaga(UUID transactionId, SagaState initialState) {
    activeSagas.put(transactionId, initialState);
    timeoutExecutor.schedule(
        () -> checkSagaTimeout(transactionId),
        SAGA_TIMEOUT_SECONDS,
        TimeUnit.SECONDS);
  }

  private void completeSaga(UUID transactionId) {
    activeSagas.remove(transactionId);
    principalSales.remove(transactionId);
  }

  void sendRollback(UUID transactionId, FailingService failingService) {
    var saga = activeSagas.remove(transactionId);
    if (saga == null) {
      return;
    }

    var sale = principalSales.remove(transactionId);
    if (sale != null) {
      rollbackPrincipalSale(sale);
    }

    if (failingService != FailingService.FINANCEIRO && saga.financeiroPayload() != null) {
      financeiroEmitter.send(saga.financeiroPayload().withRollBack());
    }
  }

  @Incoming("financeiro-in")
  @RunOnVirtualThread
  void receiveFinanceiro(AckInterface record) {
    if (!(Objects.requireNonNull(record) instanceof SagaAck financeiroAck)) {
      return;
    }

    var saga = activeSagas.get(financeiroAck.transactionId());
    if (saga == null) {
      logger.warn("Ignoring financeiro acknowledgement for unknown SAGA: {}", financeiroAck.transactionId());
      return;
    }

    var updatedSaga = new SagaState(
        saga.compradorStatus(),
        saga.compradorPayload(),
        financeiroAck.status(),
        saga.financeiroPayload(),
        saga.principalStatus(),
        saga.principalPayload());
    activeSagas.put(financeiroAck.transactionId(), updatedSaga);

    if (financeiroAck.status() == StepStatus.FAILED) {
      logger.error("Financial SAGA step failed. correlationId={}", financeiroAck.transactionId());
      sendRollback(financeiroAck.transactionId(), FailingService.FINANCEIRO);
      return;
    }

    if (financeiroAck.cpf() == null || financeiroAck.placa() == null) {
      logger.error("Financial acknowledgement is missing sale data. correlationId={}", financeiroAck.transactionId());
      sendRollback(financeiroAck.transactionId(), FailingService.FINANCEIRO);
      return;
    }

    startPrincipalSale(new PrincipalSaleContext(
        financeiroAck.transactionId(), financeiroAck.cpf(), financeiroAck.placa()));
  }

  private void startPrincipalSale(PrincipalSaleContext sale) {
    if (!activeSagas.containsKey(sale.pagamentoId())) {
      return;
    }

    if (principalSales.putIfAbsent(sale.pagamentoId(), sale) != null) {
      logger.warn("Ignoring duplicate financeiro acknowledgement. correlationId={}", sale.pagamentoId());
      return;
    }

    try (Response response = veiculosClient.comprar(sale.placa(), sale.cpf(), sale.pagamentoId())) {
      if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
        logger.error("Principal rejected sale. correlationId={}, status={}", sale.pagamentoId(), response.getStatus());
        sendRollback(sale.pagamentoId(), FailingService.PRINCIPAL);
        return;
      }
    } catch (Exception exception) {
      logger.error("Principal sale failed. correlationId={}", sale.pagamentoId(), exception);
      sendRollback(sale.pagamentoId(), FailingService.PRINCIPAL);
      return;
    }

    markPrincipalSuccess(sale.pagamentoId());
  }

  private void markPrincipalSuccess(UUID transactionId) {
    activeSagas.computeIfPresent(
        transactionId,
        (id, saga) -> {
          var completed = new SagaState(
              saga.compradorStatus(),
              saga.compradorPayload(),
              saga.financeiroStatus(),
              saga.financeiroPayload(),
              StepStatus.SUCCESS,
              saga.principalPayload());

          if (completed.completou()) {
            principalSales.remove(transactionId);
            return null;
          }

          return completed;
        });
  }

  private void checkSagaTimeout(UUID transactionId) {
    var saga = activeSagas.get(transactionId);
    if (saga == null || saga.completou()) {
      return;
    }

    logger.error("Payment SAGA timed out. correlationId={}", transactionId);
    sendRollback(transactionId, null);
  }

  private void rollbackPrincipalSale(PrincipalSaleContext sale) {
    try (Response response = veiculosClient.rollback(sale.placa(), sale.pagamentoId())) {
      logger.info(
          "Requested principal sale rollback. correlationId={}, status={}",
          sale.pagamentoId(),
          response.getStatus());
    } catch (Exception exception) {
      logger.error("Principal rollback failed. correlationId={}", sale.pagamentoId(), exception);
    }
  }
}
