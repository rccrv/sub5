package br.nom.rccrv.code.arch.usecase.comprador;

import br.nom.rccrv.code.arch.entity.CompradorEntity;
import br.nom.rccrv.code.arch.port.repository.CompradorRepositoryPort;

import java.util.List;

final public class ListarCompradoresParaAutorizarInteractorImpl implements ListarCompradoresParaAutorizarInteractor {

    private CompradorRepositoryPort compradorRepository;

    private ListarCompradoresParaAutorizarInteractorImpl() {}

    public static ListarCompradoresParaAutorizarInteractor factory(CompradorRepositoryPort compradorRepository) {
        var interactor = new ListarCompradoresParaAutorizarInteractorImpl();

        interactor.compradorRepository = compradorRepository;

        return interactor;
    }

    public List<CompradorEntity> listar() {
        return compradorRepository.findUnauthorized();
    }
}
