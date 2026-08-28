package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.port.repository.VeiculoRepositoryPort;

final public class CadastrarVeiculoInteractorImpl implements CadastrarVeiculoInteractor {

    VeiculoRepositoryPort veiculoRepository;

    private CadastrarVeiculoInteractorImpl() {}

    public static CadastrarVeiculoInteractor factory(VeiculoRepositoryPort veiculoRepository) {
        var interactor = new CadastrarVeiculoInteractorImpl();

        interactor.veiculoRepository = veiculoRepository;

        return interactor;
    }

    public VeiculoEntity cadastrar(VeiculoEntity veiculoEntity) {
        return veiculoRepository.save(veiculoEntity);
    }
}
