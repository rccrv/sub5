package br.nom.rccrv.code.domain.dto.saga;

import br.nom.rccrv.code.domain.enums.StepType;
import java.util.UUID;

public record CancelaPagamentoDto(
    UUID pagamentoId,
    UUID transactionId,
    StepType stepType,
    String type
) implements SagaDtoInterface {

  public CancelaPagamentoDto(UUID pagamentoId, UUID transactionId) {
    this(pagamentoId, transactionId, StepType.ROLLBACK, "CancelaPagamentoDto");
  }

  @Override
  public CancelaPagamentoDto withRollBack() {
    return new CancelaPagamentoDto(pagamentoId, transactionId, StepType.ROLLBACK, type);
  }
}
