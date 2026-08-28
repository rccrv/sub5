package br.nom.rccrv.code.infrastructure.concurrency;

import br.nom.rccrv.code.infrastructure.persistence.adapter.PagamentoRepositoryAdapter;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class StartupClass {

  private final Queue queue;
  private final PagamentoRepositoryAdapter repository;
  private final Clock clock;
  private final ExecutorService executor;

  @Inject
  public StartupClass(Queue queue, PagamentoRepositoryAdapter repository, Clock clock) {
    this.queue = queue;
    this.repository = repository;
    this.clock = clock;
    executor = Executors.newVirtualThreadPerTaskExecutor();
  }

  void onStart(@Observes StartupEvent event) {
    for (var index = 0; index < 4; index++) {
      executor.submit(new Dispatcher(queue.queue, repository, clock));
    }
  }
}
