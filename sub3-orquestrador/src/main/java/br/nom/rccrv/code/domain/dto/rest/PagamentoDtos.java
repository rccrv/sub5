package br.nom.rccrv.code.domain.dto.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public final class PagamentoDtos {

  private PagamentoDtos() {
  }

  public record ReservaReq(
      @NotBlank String cpf,
      @NotBlank String placa,
      @NotBlank String endereco,
      @NotBlank String cep,
      @NotNull BigDecimal quotedAmount) {
  }

  public record CriarReservaReq(
      @NotBlank String placa,
      @NotBlank String endereco,
      @NotBlank String cep) {
  }

  public record PagarReq(@NotBlank String pixCode) {
  }

  public record PagamentoResp(
      UUID id,
      UUID correlationId,
      String pixCode,
      String status,
      LocalDateTime expiresAt,
      BigDecimal settledAmount) {
  }
}
