package br.nom.rccrv.code.infrastructure.persistence.adapter;

import br.nom.rccrv.code.arch.entity.PagamentoEntity;
import br.nom.rccrv.code.arch.port.repository.PagamentoRepositoryPort;
import br.nom.rccrv.code.domain.mapper.PagamentoInputMapper;
import br.nom.rccrv.code.domain.mapper.PagamentoOutputMapper;
import br.nom.rccrv.code.infrastructure.persistence.repository.PagamentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PagamentoRepositoryAdapter implements PagamentoRepositoryPort {

  private final PagamentoRepository repository;

  @Inject
  public PagamentoRepositoryAdapter(PagamentoRepository repository) {
    this.repository = repository;
  }

  public PagamentoEntity save(PagamentoEntity entity) {
    var pagamentoJpa = PagamentoOutputMapper.paraJpa(entity);
    var persistedPagamento = entity.getId() == null
        ? repository.insert(pagamentoJpa)
        : repository.update(pagamentoJpa);

    return PagamentoInputMapper.deJpa(persistedPagamento);
  }

  public Optional<PagamentoEntity> findByCorrelationId(UUID correlationId) {
    return repository.findByCorrelationId(correlationId)
        .map(PagamentoInputMapper::deJpa);
  }

  public List<PagamentoEntity> findPendingExpired(LocalDateTime now) {
    return repository.findPendingExpired(now)
        .stream()
        .map(PagamentoInputMapper::deJpa)
        .toList();
  }
}
