package br.nom.rccrv.code.infrastructure.time;

import br.nom.rccrv.code.infrastructure.persistence.adapter.PagamentoRepositoryAdapter;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;

@ApplicationScoped
public class PagamentoExpiryScheduler {

  private final Clock clock;
  private final PagamentoRepositoryAdapter repository;

  @Inject
  public PagamentoExpiryScheduler(Clock clock, PagamentoRepositoryAdapter repository) {
    this.clock = clock;
    this.repository = repository;
  }

  @Scheduled(every = "{financeiro.payment.expiry-scan}")
  @Transactional
  void expirePendingPayments() {
    var now = LocalDateTime.now(clock);

    repository.findPendingExpired(now).forEach(
        pagamento -> {
          pagamento.expire(now);
          repository.save(pagamento);
        });
  }
}
