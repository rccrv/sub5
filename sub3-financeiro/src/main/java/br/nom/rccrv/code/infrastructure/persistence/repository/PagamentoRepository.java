package br.nom.rccrv.code.infrastructure.persistence.repository;

import br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Param;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PagamentoRepository extends CrudRepository<Pagamento, Long> {

  @Query("select pagamento from Pagamento pagamento where pagamento.correlationId = :correlationId")
  Optional<Pagamento> findByCorrelationId(@Param("correlationId") UUID correlationId);

  @Query("select pagamento from Pagamento pagamento where pagamento.status = 'PENDING' and pagamento.expiresAt <= :now")
  List<Pagamento> findPendingExpired(@Param("now") LocalDateTime now);
}
