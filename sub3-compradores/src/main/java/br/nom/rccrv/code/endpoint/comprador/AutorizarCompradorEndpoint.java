package br.nom.rccrv.code.endpoint.comprador;

import br.nom.rccrv.code.arch.controller.CompradorController;
import br.nom.rccrv.code.domain.dto.CompradorRespDto;
import br.nom.rccrv.code.infrastructure.keycloak.KeycloakAdminClient;
import br.nom.rccrv.code.infrastructure.persistence.repository.CompradorRepository;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.RestResponse;

@RequestScoped
@Path("/autorizar")
public class AutorizarCompradorEndpoint {

    CompradorRepository compradorRepository;
    KeycloakAdminClient keycloakAdminController;
    CompradorController controller;

    @Inject
    public AutorizarCompradorEndpoint(
        CompradorRepository compradorRepository,
        KeycloakAdminClient keycloakAdminController
    ) {
        this.compradorRepository = compradorRepository;
        this.keycloakAdminController = keycloakAdminController;
        this.controller = new CompradorController(compradorRepository);
        this.controller.setKeycloakAdminController(keycloakAdminController);
    }

    @Operation(
        summary = "Autorizar comprador",
        description = "Autorizar um comprador cadastrado"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Comprador autorizado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(
                    implementation = CompradorRespDto.class
                )
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "Não autorizado"
        )
    })
    @PUT
    @Path("/{cpf}")
    @Transactional
    @RunOnVirtualThread
    @RolesAllowed("funcionario")
    public RestResponse<CompradorRespDto> autorizar(
            @Parameter(description = "CPF do comprador", required = true)
            @PathParam("cpf") String cpf
    ) {
        var resp = controller.autorizar(cpf);

        return RestResponse.ok(resp);
    }
}
