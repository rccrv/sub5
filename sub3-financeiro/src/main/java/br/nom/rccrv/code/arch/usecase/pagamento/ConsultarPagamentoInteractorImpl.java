package br.nom.rccrv.code.arch.usecase.pagamento;

import br.nom.rccrv.code.arch.entity.PagamentoEntity;
import br.nom.rccrv.code.arch.port.repository.PagamentoRepositoryPort;
import java.util.*;

public final class ConsultarPagamentoInteractorImpl implements ConsultarPagamentoInteractor {

  private final PagamentoRepositoryPort repository;

  private ConsultarPagamentoInteractorImpl(PagamentoRepositoryPort repository) {
    this.repository = repository;
  }

  public static ConsultarPagamentoInteractor factory(PagamentoRepositoryPort repository) {
    return new ConsultarPagamentoInteractorImpl(repository);
  }

  public Optional<PagamentoEntity> consultar(UUID id, String cpf) {
    return repository.findByCorrelationId(id).filter(pagamento -> pagamento.belongsTo(cpf));
  }
}
