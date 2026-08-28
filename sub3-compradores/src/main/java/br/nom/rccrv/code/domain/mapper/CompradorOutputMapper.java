package br.nom.rccrv.code.domain.mapper;

import br.nom.rccrv.code.arch.entity.CompradorEntity;
import br.nom.rccrv.code.domain.dto.CompradorRespDto;
import br.nom.rccrv.code.infrastructure.persistence.entity.Comprador;

public final class CompradorOutputMapper {

    private CompradorOutputMapper() {}

    public static Comprador paraJpa(CompradorEntity entity) {
        var jpaEntity = new Comprador();
        jpaEntity.setId(entity.getCompradorId().getId());
        jpaEntity.setCpf(entity.getCpf());
        jpaEntity.setPrimeiroNome(entity.getPrimeiroNome());
        jpaEntity.setUltimoNome(entity.getUltimoNome());
        jpaEntity.setEmail(entity.getEmail());
        jpaEntity.setTelefone(entity.getTelefone());
        jpaEntity.setAutorizado(entity.getAutorizado());
        return jpaEntity;
    }

    public static CompradorRespDto paraRespDto(CompradorEntity entity) {
        return new CompradorRespDto(
            entity.getCompradorId().getId(),
            entity.getCpf(),
            entity.getPrimeiroNome(),
            entity.getUltimoNome(),
            entity.getEmail(),
            entity.getTelefone(),
            entity.getAutorizado()
        );
    }
}
