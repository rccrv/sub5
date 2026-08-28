package br.nom.rccrv.code.api.kafka;

import br.nom.rccrv.code.domain.dto.ack.SagaAck;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class OrquestradorEmitter {

  private final Emitter<SagaAck> emitter;

  @Inject
  public OrquestradorEmitter(@Channel("orquestrador-out") Emitter<SagaAck> emitter) {
    this.emitter = emitter;
  }

  public void send(SagaAck acknowledgement) {
    emitter.send(acknowledgement);
  }
}
