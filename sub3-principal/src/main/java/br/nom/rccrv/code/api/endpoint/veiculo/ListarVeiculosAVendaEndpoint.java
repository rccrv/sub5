package br.nom.rccrv.code.api.endpoint.veiculo;

import br.nom.rccrv.code.arch.controller.VeiculoController;
import br.nom.rccrv.code.domain.dto.VeiculoRespDto;
import br.nom.rccrv.code.domain.mapper.VeiculoOutputMapper;
import br.nom.rccrv.code.infrastructure.persistence.adapter.VeiculoRepositoryAdapter;
import io.smallrye.common.annotation.RunOnVirtualThread;
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
@Path("/listar-venda")
public class ListarVeiculosAVendaEndpoint {

    VeiculoRepositoryAdapter veiculoRepository;
    VeiculoController controller;

    @Inject
    public ListarVeiculosAVendaEndpoint(VeiculoRepositoryAdapter veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
        this.controller = new VeiculoController(veiculoRepository);
    }

    @Operation(
        summary = "Listar veículos a venda",
        description = "Listar veículos a venda cadastrados no sistema CarsGalore"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Lista de veículos retornada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(
                    implementation = VeiculoRespDto.class,
                    type = SchemaType.ARRAY
                )
            )
        )
        ,
        @APIResponse(
            responseCode = "401",
            description = "Não autorizado"
        )
    })
    @GET
    @RunOnVirtualThread
    public RestResponse<List<VeiculoRespDto>> listar() {
        var resp = controller.listarVeiculosAVenda().stream()
            .map(VeiculoOutputMapper::paraRespDto)
            .toList();

        return RestResponse.ok(resp);
    }
}
