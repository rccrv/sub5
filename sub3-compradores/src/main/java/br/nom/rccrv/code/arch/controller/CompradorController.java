package br.nom.rccrv.code.arch.controller;

import br.nom.rccrv.code.arch.adapter.comprador.CompradorInputAdapter;
import br.nom.rccrv.code.arch.adapter.comprador.CompradorOutputAdapter;
import br.nom.rccrv.code.arch.usecase.comprador.AutorizarCompradorInteractorImpl;
import br.nom.rccrv.code.arch.usecase.comprador.CadastrarCompradorInteractorImpl;
import br.nom.rccrv.code.arch.usecase.comprador.ListarCompradoresParaAutorizarInteractorImpl;
import br.nom.rccrv.code.domain.dto.CompradorReqDto;
import br.nom.rccrv.code.domain.dto.CompradorRespDto;
import br.nom.rccrv.code.infrastructure.keycloak.KeycloakAdminClient;
import br.nom.rccrv.code.infrastructure.persistence.repository.CompradorRepository;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

public class CompradorController {

    CompradorRepository compradorRepository;
    KeycloakAdminClient keycloakAdminController;

    public CompradorController(CompradorRepository compradorRepository) {
        this.compradorRepository = compradorRepository;
    }

    public void setKeycloakAdminController(KeycloakAdminClient keycloakAdminController) {
        this.keycloakAdminController = keycloakAdminController;
    }

    public CompradorRespDto cadastrar(CompradorReqDto req) {
        var interactor = CadastrarCompradorInteractorImpl.factory(compradorRepository);
        var compradorEntity = CompradorInputAdapter.deReqDto(req);

        return CompradorOutputAdapter.paraRespDto(interactor.cadastrar(compradorEntity));
    }

    public List<CompradorRespDto> listar() {
        var interactor = ListarCompradoresParaAutorizarInteractorImpl.factory(compradorRepository);

        return interactor.listar().stream().map(CompradorOutputAdapter::paraRespDto).toList();
    }

    public CompradorRespDto autorizar(String cpf) {
        var interactor = AutorizarCompradorInteractorImpl.factory(compradorRepository, keycloakAdminController);
        var cpfNormalizado = cpf.replaceAll("\\D", "");
        var cpfDigitos = cpfNormalizado
            .chars()
            .mapToObj(c -> c - '0')
            .toList();
        var cpfValido = CompradorReqDto.cpfValido(cpfDigitos);

        if (!cpfValido) {
            throw new IllegalArgumentException("CPF inválido");
        }

        return CompradorOutputAdapter.paraRespDto(
            interactor.autorizar(cpfNormalizado).orElseThrow(() -> new NotFoundException("Comprador não encontrado"))
        );
    }
}
