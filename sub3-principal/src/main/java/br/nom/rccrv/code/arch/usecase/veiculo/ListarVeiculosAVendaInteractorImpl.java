package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.adapter.veiculo.VeiculoInputAdapter;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;

import java.util.List;

final public class ListarVeiculosAVendaInteractorImpl implements ListarVeiculosAVendaInteractor {

    private VeiculoRepository veiculoRepository;

    private ListarVeiculosAVendaInteractorImpl() {}

    public static ListarVeiculosAVendaInteractor factory(VeiculoRepository veiculoRepository) {
        var interactor = new ListarVeiculosAVendaInteractorImpl();

        interactor.veiculoRepository = veiculoRepository;

        return interactor;
    }

    public List<VeiculoEntity> listar() {
        return veiculoRepository.listarVeiculosAVenda().stream().map(VeiculoInputAdapter::deJpa).toList();
    }
}
