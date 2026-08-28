package br.nom.rccrv.code.domain.mapper;

import br.nom.rccrv.code.arch.entity.CompradorEntity;
import br.nom.rccrv.code.arch.entity.CompradorId;
import br.nom.rccrv.code.domain.dto.CompradorReqDto;
import br.nom.rccrv.code.infrastructure.persistence.entity.Comprador;

public final class CompradorInputMapper {

    private CompradorInputMapper() {}

    public static CompradorEntity deReqDto(CompradorReqDto dto) {
        return new CompradorEntity(
            new CompradorId(null),
            dto.cpf(),
            dto.primeiroNome(),
            dto.ultimoNome(),
            dto.email(),
            dto.telefone(),
            false
        );
    }

    public static CompradorEntity deJpa(Comprador jpaEntity) {
        return new CompradorEntity(
            new CompradorId(jpaEntity.getId()),
            jpaEntity.getCpf(),
            jpaEntity.getPrimeiroNome(),
            jpaEntity.getUltimoNome(),
            jpaEntity.getEmail(),
            jpaEntity.getTelefone(),
            jpaEntity.getAutorizado()
        );
    }
}
