package br.nom.rccrv.code.arch.port.repository;

import br.nom.rccrv.code.arch.entity.PagamentoEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepositoryPort {

  PagamentoEntity save(PagamentoEntity pagamento);

  Optional<PagamentoEntity> findByCorrelationId(UUID id);

  List<PagamentoEntity> findPendingExpired(LocalDateTime now);
}
