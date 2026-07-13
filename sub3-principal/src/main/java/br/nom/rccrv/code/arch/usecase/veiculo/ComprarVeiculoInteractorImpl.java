package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.adapter.veiculo.VeiculoInputAdapter;
import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;

import java.util.Optional;

final public class ComprarVeiculoInteractorImpl implements ComprarVeiculoInteractor {

    private VeiculoRepository veiculoRepository;

    private ComprarVeiculoInteractorImpl() {}

    public static ComprarVeiculoInteractor factory(VeiculoRepository veiculoRepository) {
        var interactor = new ComprarVeiculoInteractorImpl();

        interactor.veiculoRepository = veiculoRepository;

        return interactor;
    }

    public Optional<VeiculoEntity> comprar(String placa, String cpf) {
        var jpaEntityOpt = Optional.ofNullable(veiculoRepository.findByPlaca(placa));

        if (jpaEntityOpt.isEmpty()) {
            return Optional.empty();
        }

        var jpaEntity = jpaEntityOpt.get();

        jpaEntity.setCompradorCpf(cpf);
        jpaEntity.setVendido(true);

        veiculoRepository.update(jpaEntity);

        return Optional.of(VeiculoInputAdapter.deJpa(jpaEntity));
    }
}
