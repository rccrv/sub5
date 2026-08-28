package br.nom.rccrv.code.domain.dto.pagamento;

import jakarta.validation.constraints.NotBlank;

public record PagarPagamentoReqDto(@NotBlank String pixCode) {}
