package br.nom.rccrv.code.endpoint.veiculo;

import br.nom.rccrv.code.arch.controller.VeiculoController;
import br.nom.rccrv.code.domain.dto.VeiculoReqDto;
import br.nom.rccrv.code.domain.dto.VeiculoRespDto;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
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
@Path("/editar")
public class EditarVeiculoEndpoint {

    VeiculoRepository veiculoRepository;
    VeiculoController controller;

    @Inject
    public EditarVeiculoEndpoint(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
        this.controller = new VeiculoController(veiculoRepository);
    }

    @Operation(
        summary = "Edita veículo",
        description = "Edita um veículo cadastrado no sistema CarsGalore"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Veículo editado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(
                    implementation = VeiculoRespDto.class
                )
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Erro ao editar veículo"
        ),
        @APIResponse(
            responseCode = "401",
            description = "Não autorizado"
        )
    })
    @PUT
    @Path("/{placa}")
    @Transactional
    @RunOnVirtualThread
    @RolesAllowed("funcionario")
    public RestResponse<VeiculoRespDto> editar(
            @Parameter(description = "Placa do veículo", required = true)
            @PathParam("placa")
            @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z][0-9]{2}$") String placa,
            @Valid VeiculoReqDto req
    ) {
        var resp = controller.atualizar(placa, req);

        return RestResponse.ok(resp);
    }
}
