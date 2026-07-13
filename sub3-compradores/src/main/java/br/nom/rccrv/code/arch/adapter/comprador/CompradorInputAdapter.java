package br.nom.rccrv.code.arch.adapter.comprador;

import br.nom.rccrv.code.arch.entity.CompradorEntity;
import br.nom.rccrv.code.arch.entity.CompradorId;
import br.nom.rccrv.code.domain.dto.CompradorReqDto;
import br.nom.rccrv.code.infrastructure.persistence.entity.Comprador;

public class CompradorInputAdapter {

    private CompradorInputAdapter() {}

    public static CompradorEntity deReqDto(CompradorReqDto dto) {
        var compradorId = new CompradorId(null);

        return new CompradorEntity(
            compradorId,
            dto.cpf(),
            dto.primeiroNome(),
            dto.ultimoNome(),
            dto.email(),
            dto.telefone(),
            false
        );
    }

    public static CompradorEntity deJpa(Comprador jpaEntity) {
        var compradorId = new CompradorId(jpaEntity.getId());

        return new CompradorEntity(
            compradorId,
            jpaEntity.getCpf(),
            jpaEntity.getPrimeiroNome(),
            jpaEntity.getUltimoNome(),
            jpaEntity.getEmail(),
            jpaEntity.getTelefone(),
            jpaEntity.getAutorizado()
        );
    }
}
