package br.nom.rccrv.code.endpoint.comprador;

import br.nom.rccrv.code.arch.controller.CompradorController;
import br.nom.rccrv.code.domain.dto.CompradorRespDto;
import br.nom.rccrv.code.infrastructure.persistence.repository.CompradorRepository;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@RequestScoped
@Path("/listar")
public class ListarCompradoresParaAutorizarEndpoint {

    CompradorRepository compradorRepository;
    CompradorController controller;

    @Inject
    public ListarCompradoresParaAutorizarEndpoint(CompradorRepository compradorRepository) {
        this.compradorRepository = compradorRepository;
        this.controller = new CompradorController(compradorRepository);
    }

    @Operation(
        summary = "Lista compradores para autorizar",
        description = "Lista compradores cadastrados, mas ainda não autorizados"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Lista de compradores",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(
                    implementation = CompradorRespDto.class,
                    type = SchemaType.ARRAY
                )
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "Não autorizado"
        )
    })
    @GET
    @RunOnVirtualThread
    @RolesAllowed("funcionario")
    public RestResponse<List<CompradorRespDto>> listar() {
        return RestResponse.ok(controller.listar());
    }
}
