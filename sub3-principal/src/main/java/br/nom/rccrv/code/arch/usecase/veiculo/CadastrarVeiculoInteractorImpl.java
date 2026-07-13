package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.adapter.veiculo.VeiculoInputAdapter;
import br.nom.rccrv.code.arch.adapter.veiculo.VeiculoOutputAdapter;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;

final public class CadastrarVeiculoInteractorImpl implements CadastrarVeiculoInteractor {

    VeiculoRepository veiculoRepository;

    private CadastrarVeiculoInteractorImpl() {}

    public static CadastrarVeiculoInteractor factory(VeiculoRepository veiculoRepository) {
        var interactor = new CadastrarVeiculoInteractorImpl();

        interactor.veiculoRepository = veiculoRepository;

        return interactor;
    }

    public VeiculoEntity cadastrar(VeiculoEntity veiculoEntity) {
        var veiculoJpa = VeiculoOutputAdapter.paraJpa(veiculoEntity);

        return VeiculoInputAdapter.deJpa(veiculoRepository.insert(veiculoJpa));
    }
}
