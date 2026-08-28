package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.port.repository.VeiculoRepositoryPort;

import java.util.List;

final public class ListarVeiculosVendidosInteractorImpl implements ListarVeiculosVendidosInteractor {

    private VeiculoRepositoryPort veiculoRepository;

    private ListarVeiculosVendidosInteractorImpl() {}

    public static ListarVeiculosVendidosInteractor factory(VeiculoRepositoryPort veiculoRepository) {
        var interactor = new ListarVeiculosVendidosInteractorImpl();

        interactor.veiculoRepository = veiculoRepository;

        return interactor;
    }

    public List<VeiculoEntity> listar() {
        return veiculoRepository.findSold();
    }
}
