package br.nom.rccrv.code.infrastructure.concurrency;

import br.nom.rccrv.code.domain.dto.saga.SagaDtoInterface;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class Queue {

  public final ConcurrentLinkedQueue<SagaDtoInterface> queue;

  public Queue() {
    queue = new ConcurrentLinkedQueue<>();
  }
}
