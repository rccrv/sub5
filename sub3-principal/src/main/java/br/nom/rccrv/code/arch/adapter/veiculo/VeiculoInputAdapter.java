package br.nom.rccrv.code.arch.adapter.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.entity.VeiculoId;
import br.nom.rccrv.code.domain.dto.VeiculoReqDto;
import br.nom.rccrv.code.infrastructure.persistence.entity.Veiculo;

public class VeiculoInputAdapter {

    private VeiculoInputAdapter() {}

    public static VeiculoEntity deReqDto(VeiculoReqDto dto) {
        var veiculoId = new VeiculoId(null);

        return new VeiculoEntity(
            veiculoId,
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
        var veiculoId = new VeiculoId(jpaEntity.getId());

        return new VeiculoEntity(
            veiculoId,
            jpaEntity.getMarca(),
            jpaEntity.getModelo(),
            jpaEntity.getAno(),
            jpaEntity.getPlaca(),
            jpaEntity.getCor(),
            jpaEntity.getValor(),
            jpaEntity.getCompradorCpf(),
            jpaEntity.getVendido()
        );
    }
}
