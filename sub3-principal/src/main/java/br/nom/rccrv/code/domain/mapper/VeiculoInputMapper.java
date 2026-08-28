package br.nom.rccrv.code.domain.mapper;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.entity.VeiculoId;
import br.nom.rccrv.code.domain.dto.VeiculoReqDto;
import br.nom.rccrv.code.infrastructure.persistence.entity.Veiculo;

public final class VeiculoInputMapper {

    private VeiculoInputMapper() {}

    public static VeiculoEntity deReqDto(VeiculoReqDto dto) {
        return new VeiculoEntity(
            new VeiculoId(null),
            dto.marca(),
            dto.modelo(),
            dto.ano(),
            dto.placa(),
            dto.cor(),
            dto.valor(),
            "",
            false
        );
    }

    public static VeiculoEntity deJpa(Veiculo jpaEntity) {
        var veiculoEntity = new VeiculoEntity(
            new VeiculoId(jpaEntity.getId()),
            jpaEntity.getMarca(),
            jpaEntity.getModelo(),
            jpaEntity.getAno(),
            jpaEntity.getPlaca(),
            jpaEntity.getCor(),
            jpaEntity.getValor(),
            jpaEntity.getCompradorCpf(),
            jpaEntity.getVendido()
        );

        veiculoEntity.setPagamentoId(jpaEntity.getPagamentoId());
        return veiculoEntity;
    }
}
