package br.nom.rccrv.code.arch.usecase.pagamento;

import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface CancelarPagamentoInteractor permits CancelarPagamentoInteractorImpl {

  boolean cancelar(UUID id, LocalDateTime now);
}
