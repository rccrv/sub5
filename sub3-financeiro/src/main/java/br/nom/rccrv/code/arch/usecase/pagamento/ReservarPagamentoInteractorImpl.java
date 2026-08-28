package br.nom.rccrv.code.arch.usecase.pagamento;

import br.nom.rccrv.code.arch.entity.PagamentoEntity;
import br.nom.rccrv.code.arch.port.repository.PagamentoRepositoryPort;
import br.nom.rccrv.code.domain.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public final class ReservarPagamentoInteractorImpl implements ReservarPagamentoInteractor {

  private final PagamentoRepositoryPort repository;
  private final Duration timeout;

  private ReservarPagamentoInteractorImpl(PagamentoRepositoryPort repository, Duration timeout) {
    this.repository = repository;
    this.timeout = timeout;
  }

  public static ReservarPagamentoInteractor factory(PagamentoRepositoryPort repository, Duration timeout) {
    return new ReservarPagamentoInteractorImpl(repository, timeout);
  }

  public PagamentoEntity reservar(
      String cpf,
      String placa,
      String endereco,
      String cep,
      BigDecimal quotedAmount,
      LocalDateTime now) {
    return repository.save(
        new PagamentoEntity(
            null,
            UUID.randomUUID(),
            cpf.replaceAll("\\D", ""),
            placa,
            endereco,
            cep,
            UUID.randomUUID().toString().replace("-", ""),
            quotedAmount,
            PaymentStatus.PENDING,
            now,
            now.plus(timeout)
        )
    );
  }
}
