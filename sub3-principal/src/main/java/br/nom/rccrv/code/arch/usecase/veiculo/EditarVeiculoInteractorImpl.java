package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.adapter.veiculo.VeiculoInputAdapter;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;

import java.util.Optional;

final public class EditarVeiculoInteractorImpl implements EditarVeiculoInteractor {

    VeiculoRepository veiculoRepository;

    private EditarVeiculoInteractorImpl() {}

    public static EditarVeiculoInteractor factory(VeiculoRepository veiculoRepository) {
        var interactor = new EditarVeiculoInteractorImpl();

        interactor.veiculoRepository = veiculoRepository;

        return interactor;
    }

    public Optional<VeiculoEntity> editar(String placa, VeiculoEntity veiculoEntity) {
        var jpaEntityOpt = Optional.ofNullable(veiculoRepository.findByPlaca(placa));

        if (jpaEntityOpt.isEmpty()) {
            return Optional.empty();
        }

        var jpaEntity = jpaEntityOpt.get();

        jpaEntity.setMarca(veiculoEntity.getMarca());
        jpaEntity.setModelo(veiculoEntity.getModelo());
        jpaEntity.setAno(veiculoEntity.getAno());
        jpaEntity.setCor(veiculoEntity.getCor());
        jpaEntity.setValor(veiculoEntity.getValor());
        jpaEntity.setCompradorCpf(veiculoEntity.getCompradorCpf());
        jpaEntity.setVendido(veiculoEntity.getVendido());

        veiculoRepository.update(jpaEntity);

        return Optional.of(VeiculoInputAdapter.deJpa(jpaEntity));
    }
}
