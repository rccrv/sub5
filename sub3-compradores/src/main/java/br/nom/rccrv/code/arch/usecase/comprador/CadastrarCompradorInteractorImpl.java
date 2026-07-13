package br.nom.rccrv.code.arch.usecase.comprador;

import br.nom.rccrv.code.arch.entity.CompradorEntity;
import br.nom.rccrv.code.arch.adapter.comprador.CompradorInputAdapter;
import br.nom.rccrv.code.arch.adapter.comprador.CompradorOutputAdapter;
import br.nom.rccrv.code.infrastructure.persistence.repository.CompradorRepository;

final public class CadastrarCompradorInteractorImpl implements CadastrarCompradorInteractor {

    private CompradorRepository compradorRepository;

    private CadastrarCompradorInteractorImpl() {}

    public static CadastrarCompradorInteractor factory(
            CompradorRepository compradorRepository
    ) {
        var interactor = new CadastrarCompradorInteractorImpl();

        interactor.compradorRepository = compradorRepository;

        return interactor;
    }

    public CompradorEntity cadastrar(CompradorEntity compradorEntity) {
        var compradorJpa = CompradorOutputAdapter.paraJpa(compradorEntity);

        return CompradorInputAdapter.deJpa(compradorRepository.insert(compradorJpa));
    }
}
