package br.nom.rccrv.code.api.client;

import br.nom.rccrv.code.domain.dto.rest.PagamentoDtos;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/internal/pagamentos")
@RegisterRestClient(configKey = "financeiro")
public interface FinanceiroRestClient {
  @POST
  @Path("/reservas")
  Response reservar(PagamentoDtos.ReservaReq request);

  @POST
  @Path("/{id}/pagar")
  Response pagar(
      @PathParam("id") UUID id, @HeaderParam("X-CPF") String cpf, PagamentoDtos.PagarReq request
  );

  @GET
  @Path("/{id}")
  Response consultar(@PathParam("id") UUID id, @HeaderParam("X-CPF") String cpf);
}
