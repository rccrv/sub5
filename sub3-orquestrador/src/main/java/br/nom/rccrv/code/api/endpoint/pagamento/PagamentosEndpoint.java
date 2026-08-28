package br.nom.rccrv.code.api.endpoint.pagamento;

import br.nom.rccrv.code.api.client.FinanceiroRestClient;
import br.nom.rccrv.code.api.client.VeiculosRestClient;
import br.nom.rccrv.code.api.endpoint.ResponseForwarder;
import br.nom.rccrv.code.domain.dto.rest.PagamentoDtos;
import br.nom.rccrv.code.domain.dto.saga.CancelaPagamentoDto;
import br.nom.rccrv.code.domain.enums.StepStatus;
import br.nom.rccrv.code.domain.state.SagaState;
import br.nom.rccrv.code.infrastructure.saga.SagaOrchestrator;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/pagamentos")
public class PagamentosEndpoint {

  private final FinanceiroRestClient financeiro;
  private final VeiculosRestClient veiculos;
  private final SecurityIdentity identity;
  private final SagaOrchestrator sagaOrchestrator;

  @Inject
  public PagamentosEndpoint(
      @RestClient FinanceiroRestClient financeiro,
      @RestClient VeiculosRestClient veiculos,
      SecurityIdentity identity,
      SagaOrchestrator sagaOrchestrator) {
    this.financeiro = financeiro;
    this.veiculos = veiculos;
    this.identity = identity;
    this.sagaOrchestrator = sagaOrchestrator;
  }

  @POST
  @Path("/reservas")
  @RolesAllowed("comprador")
  public Response reservar(@Valid PagamentoDtos.CriarReservaReq request) {
    var cotacao = veiculos.cotar(request.placa());

    return ResponseForwarder.forward(
        financeiro.reservar(new PagamentoDtos.ReservaReq(
            cpf(),
            request.placa(),
            request.endereco(),
            request.cep(),
            cotacao.valor())));
  }

  @POST
  @Path("/{id}/pagar")
  @RolesAllowed("comprador")
  public Response pagar(@PathParam("id") UUID id, @Valid PagamentoDtos.PagarReq request) {
    sagaOrchestrator.registerSaga(
        id,
        new SagaState(
            StepStatus.SUCCESS,
            null,
            StepStatus.PENDING,
            new CancelaPagamentoDto(id, id),
            StepStatus.PENDING,
            null));

    return ResponseForwarder.forward(financeiro.pagar(id, cpf(), request));
  }

  @GET
  @Path("/{id}")
  @RolesAllowed("comprador")
  public Response consultar(@PathParam("id") UUID id) {
    return ResponseForwarder.forward(financeiro.consultar(id, cpf()));
  }

  private String cpf() {
    return identity.getPrincipal().getName();
  }
}
