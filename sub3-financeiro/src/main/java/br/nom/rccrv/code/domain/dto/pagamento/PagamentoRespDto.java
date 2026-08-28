package br.nom.rccrv.code.domain.dto.pagamento;

import br.nom.rccrv.code.domain.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PagamentoRespDto(
    UUID id,
    UUID correlationId,
    String pixCode,
    PaymentStatus status,
    LocalDateTime expiresAt,
    BigDecimal settledAmount
) {
}
