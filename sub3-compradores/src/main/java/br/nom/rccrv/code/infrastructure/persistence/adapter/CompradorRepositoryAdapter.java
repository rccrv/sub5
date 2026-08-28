package br.nom.rccrv.code.infrastructure.persistence.adapter;

import br.nom.rccrv.code.arch.entity.CompradorEntity;
import br.nom.rccrv.code.arch.port.repository.CompradorRepositoryPort;
import br.nom.rccrv.code.domain.mapper.CompradorInputMapper;
import br.nom.rccrv.code.domain.mapper.CompradorOutputMapper;
import br.nom.rccrv.code.infrastructure.persistence.repository.CompradorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CompradorRepositoryAdapter implements CompradorRepositoryPort {

    private final CompradorRepository repository;

    @Inject
    public CompradorRepositoryAdapter(CompradorRepository repository) {
        this.repository = repository;
    }

    @Override
    public CompradorEntity save(CompradorEntity comprador) {
        var jpaEntity = CompradorOutputMapper.paraJpa(comprador);
        var persistedEntity = comprador.getCompradorId().getId() == null
            ? repository.insert(jpaEntity)
            : repository.update(jpaEntity);

        return CompradorInputMapper.deJpa(persistedEntity);
    }

    @Override
    public Optional<CompradorEntity> findByCpf(String cpf) {
        return Optional.ofNullable(repository.findByCpf(cpf))
            .map(CompradorInputMapper::deJpa);
    }

    @Override
    public List<CompradorEntity> findUnauthorized() {
        return repository.listarCompradoresParaAutorizar().stream()
            .map(CompradorInputMapper::deJpa)
            .toList();
    }
}
