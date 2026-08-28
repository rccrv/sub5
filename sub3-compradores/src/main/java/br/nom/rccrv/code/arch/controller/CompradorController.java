package br.nom.rccrv.code.arch.controller;

import br.nom.rccrv.code.arch.entity.CompradorEntity;
import br.nom.rccrv.code.arch.port.repository.CompradorRepositoryPort;
import br.nom.rccrv.code.arch.port.service.CreateUserAuthServicePort;
import br.nom.rccrv.code.arch.usecase.comprador.AutorizarCompradorInteractorImpl;
import br.nom.rccrv.code.arch.usecase.comprador.CadastrarCompradorInteractorImpl;
import br.nom.rccrv.code.arch.usecase.comprador.ListarCompradoresParaAutorizarInteractorImpl;

import java.util.List;
import java.util.Optional;

public class CompradorController {

    private final CompradorRepositoryPort compradorRepositoryPort;
    private CreateUserAuthServicePort createUserAuthServicePort;

    public CompradorController(CompradorRepositoryPort compradorRepositoryPort) {
        this.compradorRepositoryPort = compradorRepositoryPort;
    }

    public CompradorController(
        CompradorRepositoryPort compradorRepositoryPort,
        CreateUserAuthServicePort createUserAuthServicePort
    ) {
        this.compradorRepositoryPort = compradorRepositoryPort;
        this.createUserAuthServicePort = createUserAuthServicePort;
    }

    public void setCreateUserAuthServicePort(CreateUserAuthServicePort createUserAuthServicePort) {
        this.createUserAuthServicePort = createUserAuthServicePort;
    }

    public CompradorEntity cadastrar(CompradorEntity compradorEntity) {
        var interactor = CadastrarCompradorInteractorImpl.factory(compradorRepositoryPort);

        return interactor.cadastrar(compradorEntity);
    }

    public void rollbackCadastrar(CompradorEntity compradorEntity) {
        var interactor = CadastrarCompradorInteractorImpl.factory(compradorRepositoryPort);

        interactor.rollbackCadastrar(compradorEntity);
    }

    public List<CompradorEntity> listar() {
        var interactor = ListarCompradoresParaAutorizarInteractorImpl.factory(compradorRepositoryPort);

        return interactor.listar();
    }

    public Optional<CompradorEntity> autorizar(String cpf) {
        var interactor = AutorizarCompradorInteractorImpl.factory(compradorRepositoryPort, createUserAuthServicePort);

        return interactor.autorizar(cpf);
    }

    public void rollbackAutorizar(String cpf) {
        var interactor = AutorizarCompradorInteractorImpl.factory(compradorRepositoryPort, createUserAuthServicePort);

        interactor.rollbackAutorizar(cpf);
    }
}
