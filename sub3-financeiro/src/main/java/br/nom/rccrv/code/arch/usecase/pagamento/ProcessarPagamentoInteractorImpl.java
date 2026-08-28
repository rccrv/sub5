package br.nom.rccrv.code.arch.usecase.pagamento;

import br.nom.rccrv.code.arch.entity.PagamentoEntity;
import br.nom.rccrv.code.arch.port.repository.PagamentoRepositoryPort;
import java.time.LocalDateTime;
import java.util.*;

public final class ProcessarPagamentoInteractorImpl implements ProcessarPagamentoInteractor {

  private final PagamentoRepositoryPort repository;

  private ProcessarPagamentoInteractorImpl(PagamentoRepositoryPort repository) {
    this.repository = repository;
  }

  public static ProcessarPagamentoInteractor factory(PagamentoRepositoryPort repository) {
    return new ProcessarPagamentoInteractorImpl(repository);
  }

  public Optional<PagamentoEntity> processar(UUID id, String cpf, String pix, LocalDateTime now) {
    return repository
        .findByCorrelationId(id)
        .map(
            pagamento -> {
              if (!pagamento.belongsTo(cpf)) {
                return null;
              }

              if (pagamento.expired(now)) {
                pagamento.expire(now);
                repository.save(pagamento);
                return null;
              }

              if (!pagamento.canProcess(pix)) {
                return null;
              }

              pagamento.process(now);
              pagamento.settle(pagamento.getQuotedAmount(), now);
              return repository.save(pagamento);
            });
  }
}
