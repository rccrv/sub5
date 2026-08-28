package br.nom.rccrv.code.domain.dto.ack;

import br.nom.rccrv.code.domain.enums.StepStatus;
import java.util.UUID;

public record SagaAck(
    UUID transactionId,
    StepStatus status,
    String cpf,
    String placa,
    String type
) implements AckInterface {

  public SagaAck(UUID transactionId, StepStatus status) {
    this(transactionId, status, null, null, "SagaAck");
  }
}
