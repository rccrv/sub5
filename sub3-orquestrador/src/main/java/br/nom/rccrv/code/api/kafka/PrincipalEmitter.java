package br.nom.rccrv.code.api.kafka;

import br.nom.rccrv.code.domain.dto.saga.SagaDtoInterface;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class PrincipalEmitter {

    Emitter<SagaDtoInterface> emitter;

    @Inject
    public PrincipalEmitter(@Channel("principal-out") Emitter<SagaDtoInterface> emitter) {
        this.emitter = emitter;
    }

    public void send(SagaDtoInterface record) {
        emitter.send(record);
    }
}
