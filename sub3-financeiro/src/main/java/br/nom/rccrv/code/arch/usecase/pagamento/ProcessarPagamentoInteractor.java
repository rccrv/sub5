package br.nom.rccrv.code.arch.usecase.pagamento;

import br.nom.rccrv.code.arch.entity.PagamentoEntity;
import java.time.LocalDateTime;
import java.util.*;

public sealed interface ProcessarPagamentoInteractor permits ProcessarPagamentoInteractorImpl {

  Optional<PagamentoEntity> processar(UUID id, String cpf, String pixCode, LocalDateTime now);
}
