package br.nom.rccrv.code.arch.usecase.pagamento;

import br.nom.rccrv.code.arch.port.repository.PagamentoRepositoryPort;
import java.time.LocalDateTime;
import java.util.UUID;

public final class CancelarPagamentoInteractorImpl implements CancelarPagamentoInteractor {

  private final PagamentoRepositoryPort repository;

  private CancelarPagamentoInteractorImpl(PagamentoRepositoryPort repository) {
    this.repository = repository;
  }

  public static CancelarPagamentoInteractor factory(PagamentoRepositoryPort repository) {
    return new CancelarPagamentoInteractorImpl(repository);
  }

  public boolean cancelar(UUID id, LocalDateTime now) {
    return repository
        .findByCorrelationId(id)
        .map(
            pagamento -> {
              if (!pagamento.cancel(now)) return false;
              repository.save(pagamento);
              return true;
            })
        .orElse(false);
  }
}
