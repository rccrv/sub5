package br.nom.rccrv.code.arch.usecase.pagamento;

import br.nom.rccrv.code.arch.entity.PagamentoEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public sealed interface ReservarPagamentoInteractor permits ReservarPagamentoInteractorImpl {

  PagamentoEntity reservar(
      String cpf,
      String placa,
      String endereco,
      String cep,
      BigDecimal quotedAmount,
      LocalDateTime now);
}
