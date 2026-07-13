package br.nom.rccrv.code.arch.usecase.comprador;

import br.nom.rccrv.code.arch.entity.CompradorEntity;
import br.nom.rccrv.code.arch.adapter.comprador.CompradorInputAdapter;
import br.nom.rccrv.code.infrastructure.persistence.repository.CompradorRepository;

import java.util.List;

final public class ListarCompradoresParaAutorizarInteractorImpl implements ListarCompradoresParaAutorizarInteractor {

    private CompradorRepository compradorRepository;

    private ListarCompradoresParaAutorizarInteractorImpl() {}

    public static ListarCompradoresParaAutorizarInteractor factory(CompradorRepository compradorRepository) {
        var interactor = new ListarCompradoresParaAutorizarInteractorImpl();

        interactor.compradorRepository = compradorRepository;

        return interactor;
    }

    public List<CompradorEntity> listar() {
        return compradorRepository.listarCompradoresParaAutorizar().stream().map(CompradorInputAdapter::deJpa).toList();
    }
}
