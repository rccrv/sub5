package br.nom.rccrv.code.domain.mapper;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.domain.dto.VeiculoRespDto;
import br.nom.rccrv.code.infrastructure.persistence.entity.Veiculo;

public final class VeiculoOutputMapper {

    private VeiculoOutputMapper() {}

    public static Veiculo paraJpa(VeiculoEntity entity) {
        var jpaEntity = new Veiculo();
        jpaEntity.setId(entity.getVeiculoId().getId());
        jpaEntity.setMarca(entity.getMarca());
        jpaEntity.setModelo(entity.getModelo());
        jpaEntity.setAno(entity.getAno());
        jpaEntity.setPlaca(entity.getPlaca());
        jpaEntity.setCor(entity.getCor());
        jpaEntity.setValor(entity.getValor());
        jpaEntity.setCompradorCpf(entity.getCompradorCpf());
        jpaEntity.setPagamentoId(entity.getPagamentoId());
        jpaEntity.setVendido(entity.getVendido());
        return jpaEntity;
    }

    public static VeiculoRespDto paraRespDto(VeiculoEntity entity) {
        return new VeiculoRespDto(
            entity.getVeiculoId().getId(),
            entity.getMarca(),
            entity.getModelo(),
            entity.getAno(),
            entity.getPlaca(),
            entity.getCor(),
            entity.getValor(),
            entity.getCompradorCpf(),
            entity.getVendido()
        );
    }
}
