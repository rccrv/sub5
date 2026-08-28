package br.nom.rccrv.code.arch.usecase.pagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface LiquidarPagamentoInteractor permits LiquidarPagamentoInteractorImpl {

  boolean liquidar(UUID id, BigDecimal amount, LocalDateTime now);
}
