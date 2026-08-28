package br.nom.rccrv.code.api.endpoint.veiculo;

import br.nom.rccrv.code.arch.controller.VeiculoController;
import br.nom.rccrv.code.infrastructure.persistence.adapter.VeiculoRepositoryAdapter;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.util.UUID;

@RequestScoped
@Path("/interno/veiculos")
public class RollbackCompraVeiculoEndpoint {

  private final VeiculoController controller;

  @Inject
  public RollbackCompraVeiculoEndpoint(VeiculoRepositoryAdapter repository) {
    controller = new VeiculoController(repository);
  }

  @PUT
  @Path("/{placa}/rollback")
  @Transactional
  public Response rollback(
      @PathParam("placa")
      @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z][0-9]{2}$") String placa,
      @HeaderParam("X-Pagamento-Id") @NotNull UUID pagamentoId
  ) {
    if (!controller.rollbackCompra(placa, pagamentoId)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.noContent().build();
  }
}
