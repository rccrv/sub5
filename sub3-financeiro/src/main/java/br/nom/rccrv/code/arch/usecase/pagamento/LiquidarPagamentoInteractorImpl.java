package br.nom.rccrv.code.arch.usecase.pagamento;

import br.nom.rccrv.code.arch.port.repository.PagamentoRepositoryPort;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public final class LiquidarPagamentoInteractorImpl implements LiquidarPagamentoInteractor {

  private final PagamentoRepositoryPort repository;

  private LiquidarPagamentoInteractorImpl(PagamentoRepositoryPort repository) {
    this.repository = repository;
  }

  public static LiquidarPagamentoInteractor factory(PagamentoRepositoryPort repository) {
    return new LiquidarPagamentoInteractorImpl(repository);
  }

  public boolean liquidar(UUID id, BigDecimal amount, LocalDateTime now) {
    return repository
        .findByCorrelationId(id)
        .map(
            pagamento -> {
              if (!pagamento.settle(amount, now)) return false;
              repository.save(pagamento);
              return true;
            })
        .orElse(false);
  }
}
