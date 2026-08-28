package br.nom.rccrv.code.api.kafka;

import br.nom.rccrv.code.domain.dto.saga.CancelaPagamentoDto;
import br.nom.rccrv.code.domain.dto.saga.SagaDtoInterface;
import br.nom.rccrv.code.infrastructure.concurrency.Queue;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Objects;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class OrquestradorReceiver {

  private final Queue queue;
  private final Logger logger;

  @Inject
  public OrquestradorReceiver(Queue queue) {
    this.queue = queue;
    logger = LoggerFactory.getLogger(OrquestradorReceiver.class);
  }

  @Incoming("orquestrador-in")
  @RunOnVirtualThread
  void receive(SagaDtoInterface record) {
    if (Objects.requireNonNull(record) instanceof CancelaPagamentoDto cancellation) {
      queue.queue.offer(cancellation);
      return;
    }

    logger.warn("Ignoring unsupported financeiro SAGA command: {}", record.type());
  }
}
