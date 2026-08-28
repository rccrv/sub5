package br.nom.rccrv.code.domain.mapper;

import br.nom.rccrv.code.arch.entity.PagamentoEntity;
import br.nom.rccrv.code.domain.dto.pagamento.PagamentoRespDto;
import br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento;

public final class PagamentoOutputMapper {

  private PagamentoOutputMapper() {}

  public static PagamentoRespDto paraRespDto(PagamentoEntity entity) {
    return new PagamentoRespDto(
        entity.getCorrelationId(),
        entity.getCorrelationId(),
        entity.getPixCode(),
        entity.getStatus(),
        entity.getExpiresAt(),
        entity.getSettledAmount()
    );
  }

  public static Pagamento paraJpa(PagamentoEntity entity) {
    var pagamento = new Pagamento();

    pagamento.id = entity.getId();
    pagamento.correlationId = entity.getCorrelationId();
    pagamento.cpf = entity.getCpf();
    pagamento.placa = entity.getPlaca();
    pagamento.endereco = entity.getEndereco();
    pagamento.cep = entity.getCep();
    pagamento.pixCode = entity.getPixCode();
    pagamento.quotedAmount = entity.getQuotedAmount();
    pagamento.status = entity.getStatus();
    pagamento.createdAt = entity.getCreatedAt();
    pagamento.expiresAt = entity.getExpiresAt();
    pagamento.updatedAt = entity.getUpdatedAt();
    pagamento.settledAmount = entity.getSettledAmount();
    pagamento.version = entity.getVersion();

    return pagamento;
  }
}
