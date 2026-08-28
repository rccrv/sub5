package br.nom.rccrv.code.infrastructure.concurrency;

import br.nom.rccrv.code.arch.controller.PagamentoController;
import br.nom.rccrv.code.domain.dto.saga.CancelaPagamentoDto;
import br.nom.rccrv.code.domain.dto.saga.SagaDtoInterface;
import br.nom.rccrv.code.domain.enums.StepType;
import br.nom.rccrv.code.infrastructure.persistence.adapter.PagamentoRepositoryAdapter;
import io.quarkus.narayana.jta.QuarkusTransaction;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatcher implements Runnable {

  private final ConcurrentLinkedQueue<SagaDtoInterface> queue;
  private final PagamentoController controller;
  private final Clock clock;
  private final Logger logger;

  public Dispatcher(
      ConcurrentLinkedQueue<SagaDtoInterface> queue,
      PagamentoRepositoryAdapter repository,
      Clock clock) {
    this.queue = queue;
    controller = new PagamentoController(repository, java.time.Duration.ZERO);
    this.clock = clock;
    logger = LoggerFactory.getLogger(Dispatcher.class);
  }

  @Override
  public void run() {
    var shouldRun = new AtomicBoolean(true);

    while (shouldRun.get()) {
      var command = queue.poll();
      if (command == null) {
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(50));
        continue;
      }

      dispatch(command);
    }
  }

  private void dispatch(SagaDtoInterface command) {
    if (command instanceof CancelaPagamentoDto cancellation
        && cancellation.stepType() == StepType.ROLLBACK) {
      cancel(cancellation);
      return;
    }

    logger.warn("Ignoring unsupported financeiro SAGA command: {}", command);
  }

  private void cancel(CancelaPagamentoDto command) {
    try {
      QuarkusTransaction.requiringNew().run(
          () -> controller.cancel(command.pagamentoId(), LocalDateTime.now(clock)));
      logger.info(
          "Cancelled payment through SAGA compensation. paymentId={}, transactionId={}",
          command.pagamentoId(),
          command.transactionId());
    } catch (Exception exception) {
      logger.error(
          "Could not cancel payment through SAGA compensation. paymentId={}, transactionId={}",
          command.pagamentoId(),
          command.transactionId(),
          exception);
    }
  }
}
