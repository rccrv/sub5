package br.nom.rccrv.code.arch.usecase.pagamento;

import br.nom.rccrv.code.arch.entity.PagamentoEntity;
import java.util.*;

public sealed interface ConsultarPagamentoInteractor permits ConsultarPagamentoInteractorImpl {

  Optional<PagamentoEntity> consultar(UUID id, String cpf);
}
