package br.nom.rccrv.code.infrastructure.persistence.adapter;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.port.repository.VeiculoRepositoryPort;
import br.nom.rccrv.code.domain.mapper.VeiculoInputMapper;
import br.nom.rccrv.code.domain.mapper.VeiculoOutputMapper;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class VeiculoRepositoryAdapter implements VeiculoRepositoryPort {

    private final VeiculoRepository repository;

    @Inject
    public VeiculoRepositoryAdapter(VeiculoRepository repository) {
        this.repository = repository;
    }

    @Override
    public VeiculoEntity save(VeiculoEntity veiculo) {
        var jpaEntity = VeiculoOutputMapper.paraJpa(veiculo);
        var persistedEntity = veiculo.getVeiculoId().getId() == null
            ? repository.insert(jpaEntity)
            : repository.update(jpaEntity);

        return VeiculoInputMapper.deJpa(persistedEntity);
    }

    @Override
    public Optional<VeiculoEntity> findByPlaca(String placa) {
        return Optional.ofNullable(repository.findByPlaca(placa))
            .map(VeiculoInputMapper::deJpa);
    }

    @Override
    public List<VeiculoEntity> findAvailable() {
        return repository.listarVeiculosAVenda().stream()
            .map(VeiculoInputMapper::deJpa)
            .toList();
    }

    @Override
    public List<VeiculoEntity> findSold() {
        return repository.listarVeiculosVendidos().stream()
            .map(VeiculoInputMapper::deJpa)
            .toList();
    }

    @Override
    public boolean sellIfAvailable(String placa, String cpf, UUID pagamentoId) {
        return repository.sellIfAvailable(placa, cpf, pagamentoId) == 1;
    }

    @Override
    public boolean rollbackSale(String placa, UUID pagamentoId) {
        return repository.rollbackSale(placa, pagamentoId) == 1;
    }
}
