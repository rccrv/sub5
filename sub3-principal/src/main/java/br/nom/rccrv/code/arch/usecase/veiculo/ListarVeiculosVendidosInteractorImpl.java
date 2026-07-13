package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.adapter.veiculo.VeiculoInputAdapter;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;

import java.util.List;

final public class ListarVeiculosVendidosInteractorImpl implements ListarVeiculosVendidosInteractor {

    private VeiculoRepository veiculoRepository;

    private ListarVeiculosVendidosInteractorImpl() {}

    public static ListarVeiculosVendidosInteractor factory(VeiculoRepository veiculoRepository) {
        var interactor = new ListarVeiculosVendidosInteractorImpl();

        interactor.veiculoRepository = veiculoRepository;

        return interactor;
    }

    public List<VeiculoEntity> listar() {
        return veiculoRepository.listarVeiculosVendidos().stream().map(VeiculoInputAdapter::deJpa).toList();
    }
}
