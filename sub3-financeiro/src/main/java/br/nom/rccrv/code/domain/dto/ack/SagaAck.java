package br.nom.rccrv.code.domain.dto.ack;

import java.util.UUID;

public record SagaAck(
    UUID transactionId,
    SagaStatus status,
    String cpf,
    String placa,
    String type
) {

  public SagaAck(UUID transactionId, SagaStatus status, String cpf, String placa) {
    this(transactionId, status, cpf, placa, "SagaAck");
  }

  public enum SagaStatus {
    SUCCESS,
    FAILED
  }
}
