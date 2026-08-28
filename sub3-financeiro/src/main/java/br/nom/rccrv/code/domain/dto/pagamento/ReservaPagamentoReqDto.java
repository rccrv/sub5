package br.nom.rccrv.code.domain.dto.pagamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ReservaPagamentoReqDto(
    @NotBlank String cpf,
    @NotBlank String placa,
    @NotBlank String endereco,
    @NotBlank String cep,
    @NotNull BigDecimal quotedAmount
) {
}
