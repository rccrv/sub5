package br.nom.rccrv.code.arch.port.repository;

import br.nom.rccrv.code.arch.entity.CompradorEntity;

import java.util.List;
import java.util.Optional;

public interface CompradorRepositoryPort {

    CompradorEntity save(CompradorEntity comprador);
    Optional<CompradorEntity> findByCpf(String cpf);
    List<CompradorEntity> findUnauthorized();
}
