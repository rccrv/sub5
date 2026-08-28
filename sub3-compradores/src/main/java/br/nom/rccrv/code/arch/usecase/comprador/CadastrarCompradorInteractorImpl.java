package br.nom.rccrv.code.arch.usecase.comprador;

import br.nom.rccrv.code.arch.entity.CompradorEntity;
import br.nom.rccrv.code.arch.port.repository.CompradorRepositoryPort;

final public class CadastrarCompradorInteractorImpl implements CadastrarCompradorInteractor {

    private CompradorRepositoryPort compradorRepository;

    private CadastrarCompradorInteractorImpl() {}

    public static CadastrarCompradorInteractor factory(
            CompradorRepositoryPort compradorRepository
    ) {
        var interactor = new CadastrarCompradorInteractorImpl();

        interactor.compradorRepository = compradorRepository;

        return interactor;
    }

    public CompradorEntity cadastrar(CompradorEntity compradorEntity) {
        return compradorRepository.save(compradorEntity);
    }
}
