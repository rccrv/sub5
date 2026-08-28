package br.nom.rccrv.code.api.endpoint.veiculo;

import br.nom.rccrv.code.arch.controller.VeiculoController;
import br.nom.rccrv.code.domain.dto.VeiculoRespDto;
import br.nom.rccrv.code.domain.mapper.VeiculoOutputMapper;
import br.nom.rccrv.code.infrastructure.persistence.adapter.VeiculoRepositoryAdapter;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestResponse;
import java.util.UUID;

@RequestScoped
@Path("/comprar")
public class ComprarVeiculoEndpoint {

    VeiculoRepositoryAdapter veiculoRepository;
    VeiculoController controller;

    @Inject
    public ComprarVeiculoEndpoint(VeiculoRepositoryAdapter veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
        this.controller = new VeiculoController(veiculoRepository);
    }

    @PUT
    @Path("/{placa}")
    @Transactional
    @RunOnVirtualThread
    public RestResponse<VeiculoRespDto> comprar(
            @Parameter(description = "Placa do veículo", required = true)
            @PathParam("placa")
            @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z][0-9]{2}$") String placa,
            @Parameter(description = "CPF do comprador", required = true)
            @HeaderParam("X-CPF")
            @NotBlank String cpf,
            @HeaderParam("X-Pagamento-Id") UUID pagamentoId
    ) {
        var resp = controller.comprar(placa, cpf, pagamentoId)
            .map(VeiculoOutputMapper::paraRespDto)
            .orElseThrow(() -> new NotFoundException("Veículo não encontrado"));

        return RestResponse.ok(resp);
    }
}
