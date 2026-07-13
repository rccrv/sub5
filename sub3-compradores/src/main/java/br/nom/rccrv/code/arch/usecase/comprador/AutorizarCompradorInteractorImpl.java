package br.nom.rccrv.code.arch.usecase.comprador;

import br.nom.rccrv.code.arch.entity.CompradorEntity;
import br.nom.rccrv.code.arch.adapter.comprador.CompradorInputAdapter;
import br.nom.rccrv.code.infrastructure.keycloak.KeycloakAdminClient;
import br.nom.rccrv.code.infrastructure.persistence.repository.CompradorRepository;

import java.util.Optional;

final public class AutorizarCompradorInteractorImpl implements AutorizarCompradorInteractor {

    private CompradorRepository compradorRepository;
    private KeycloakAdminClient keycloakAdminController;

    private AutorizarCompradorInteractorImpl() {}

    public static AutorizarCompradorInteractor factory(
            CompradorRepository compradorRepository,
            KeycloakAdminClient keycloakAdminController
    ) {
        var interactor = new AutorizarCompradorInteractorImpl();

        interactor.compradorRepository = compradorRepository;
        interactor.keycloakAdminController = keycloakAdminController;

        return interactor;
    }

    public Optional<CompradorEntity> autorizar(String cpf) {
        var compradorJpa = Optional.ofNullable(compradorRepository.findByCpf(cpf));

        if (compradorJpa.isEmpty()) {
            return Optional.empty();
        }

        keycloakAdminController.criarComprador(cpf);

        compradorJpa.get().setAutorizado(true);
        compradorRepository.update(compradorJpa.get());

        return Optional.of(CompradorInputAdapter.deJpa(compradorRepository.findByCpf(cpf)));
    }
}
