package br.nom.rccrv.code.arch.usecase.comprador;

import br.nom.rccrv.code.arch.entity.CompradorEntity;
import br.nom.rccrv.code.arch.port.repository.CompradorRepositoryPort;
import br.nom.rccrv.code.arch.port.service.CreateUserAuthServicePort;

import java.util.Optional;

final public class AutorizarCompradorInteractorImpl implements AutorizarCompradorInteractor {

    private CompradorRepositoryPort compradorRepository;
    private CreateUserAuthServicePort createUserAuthServicePort;

    private AutorizarCompradorInteractorImpl() {}

    public static AutorizarCompradorInteractor factory(
            CompradorRepositoryPort compradorRepository,
            CreateUserAuthServicePort keycloakAdminController
    ) {
        var interactor = new AutorizarCompradorInteractorImpl();

        interactor.compradorRepository = compradorRepository;
        interactor.createUserAuthServicePort = keycloakAdminController;

        return interactor;
    }

    public Optional<CompradorEntity> autorizar(String cpf) {
        var comprador = compradorRepository.findByCpf(cpf);

        if (comprador.isEmpty()) {
            return Optional.empty();
        }

        createUserAuthServicePort.criarComprador(cpf);

        comprador.get().setAutorizado(true);

        return Optional.of(compradorRepository.save(comprador.get()));
    }
}
