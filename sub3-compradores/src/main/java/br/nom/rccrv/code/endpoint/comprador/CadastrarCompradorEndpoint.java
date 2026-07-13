package br.nom.rccrv.code.endpoint.comprador;

import br.nom.rccrv.code.arch.controller.CompradorController;
import br.nom.rccrv.code.domain.dto.CompradorReqDto;
import br.nom.rccrv.code.domain.dto.CompradorRespDto;
import br.nom.rccrv.code.infrastructure.persistence.repository.CompradorRepository;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.RestResponse;

@RequestScoped
@Path("/cadastrar")
public class CadastrarCompradorEndpoint {

    CompradorRepository compradorRepository;
    CompradorController controller;

    @Inject
    public CadastrarCompradorEndpoint(CompradorRepository compradorRepository) {
        this.compradorRepository = compradorRepository;
        this.controller = new CompradorController(compradorRepository);
    }

    @Operation(
        summary = "Cadastra comprador",
        description = "Cadastra comprador para autorização posterior"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Comprador cadastrado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(
                    implementation = CompradorRespDto.class
                )
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Erro ao cadastrar comprador"
        )
    })
    @POST
    @Transactional
    @RunOnVirtualThread
    public RestResponse<CompradorRespDto> cadastrar(@Valid CompradorReqDto req) {
        var resp = controller.cadastrar(req);

        return RestResponse.status(Response.Status.CREATED, resp);
    }
}
