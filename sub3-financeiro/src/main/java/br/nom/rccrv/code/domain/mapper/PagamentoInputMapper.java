package br.nom.rccrv.code.domain.mapper;

import br.nom.rccrv.code.arch.entity.PagamentoEntity;
import br.nom.rccrv.code.domain.dto.pagamento.ReservaPagamentoReqDto;
import br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento;

public final class PagamentoInputMapper {

  private PagamentoInputMapper() {}

  public static PagamentoEntity deReqDto(ReservaPagamentoReqDto dto) {
    return new PagamentoEntity(
        null,
        null,
        dto.cpf(),
        dto.placa(),
        dto.endereco(),
        dto.cep(),
        null,
        dto.quotedAmount(),
        null,
        null,
        null
    );
  }

  public static PagamentoEntity deJpa(Pagamento pagamento) {
    var pagamentoEntity = new PagamentoEntity(
        pagamento.id,
        pagamento.correlationId,
        pagamento.cpf,
        pagamento.placa,
        pagamento.endereco,
        pagamento.cep,
        pagamento.pixCode,
        pagamento.quotedAmount,
        pagamento.status,
        pagamento.createdAt,
        pagamento.expiresAt
    );

    pagamentoEntity.restorePersistenceState(
        pagamento.settledAmount,
        pagamento.updatedAt,
        pagamento.version);
    return pagamentoEntity;
  }
}
