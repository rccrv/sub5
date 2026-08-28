package br.nom.rccrv.code.api.endpoint.veiculo;

import br.nom.rccrv.code.arch.controller.VeiculoController;
import br.nom.rccrv.code.domain.dto.VeiculoReqDto;
import br.nom.rccrv.code.domain.dto.VeiculoRespDto;
import br.nom.rccrv.code.domain.mapper.VeiculoInputMapper;
import br.nom.rccrv.code.domain.mapper.VeiculoOutputMapper;
import br.nom.rccrv.code.infrastructure.persistence.adapter.VeiculoRepositoryAdapter;
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
public class CadastrarVeiculoEndpoint {

    VeiculoRepositoryAdapter veiculoRepository;
    VeiculoController controller;

    @Inject
    public CadastrarVeiculoEndpoint(VeiculoRepositoryAdapter veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
        this.controller = new VeiculoController(veiculoRepository);
    }

    @Operation(
        summary = "Cadastra veículo",
        description = "Cadastra veículo no sistema CarsGalore"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Veículo cadastrado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(
                    implementation = VeiculoRespDto.class
                )
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Erro ao cadastrar veículo"
        ),
        @APIResponse(
            responseCode = "401",
            description = "Não autorizado"
        )
    })
    @POST
    @Transactional
    @RunOnVirtualThread
    public RestResponse<VeiculoRespDto> cadastrar(@Valid VeiculoReqDto req) {
        var resp = VeiculoOutputMapper.paraRespDto(
            controller.cadastrar(VeiculoInputMapper.deReqDto(req))
        );

        return RestResponse.status(Response.Status.CREATED, resp);
    }
}
