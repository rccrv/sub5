package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.port.repository.VeiculoRepositoryPort;

import java.util.List;

final public class ListarVeiculosAVendaInteractorImpl implements ListarVeiculosAVendaInteractor {

    private VeiculoRepositoryPort veiculoRepository;

    private ListarVeiculosAVendaInteractorImpl() {}

    public static ListarVeiculosAVendaInteractor factory(VeiculoRepositoryPort veiculoRepository) {
        var interactor = new ListarVeiculosAVendaInteractorImpl();

        interactor.veiculoRepository = veiculoRepository;

        return interactor;
    }

    public List<VeiculoEntity> listar() {
        return veiculoRepository.findAvailable();
    }
}
