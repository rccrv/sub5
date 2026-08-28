package br.nom.rccrv.code.api.endpoint.veiculo;

import br.nom.rccrv.code.api.client.VeiculosRestClient;
import br.nom.rccrv.code.api.endpoint.ResponseForwarder;
import br.nom.rccrv.code.domain.dto.rest.VeiculoReqDto;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@RequestScoped
@Path("/veiculos")
public class VeiculosEndpoint {

    private final VeiculosRestClient veiculosClient;
    private final SecurityIdentity identity;

    @Inject
    public VeiculosEndpoint(
        @RestClient VeiculosRestClient veiculosClient,
        SecurityIdentity identity
    ) {
        this.veiculosClient = veiculosClient;
        this.identity = identity;
    }

    @POST
    @Path("/cadastrar")
    @RolesAllowed("funcionario")
    public Response cadastrar(@Valid VeiculoReqDto req) {
        return ResponseForwarder.forward(veiculosClient.cadastrar(req));
    }

    @PUT
    @Path("/editar/{placa}")
    @RolesAllowed("funcionario")
    public Response editar(@PathParam("placa") String placa, @Valid VeiculoReqDto req) {
        return ResponseForwarder.forward(veiculosClient.editar(placa, req));
    }

    @GET
    @Path("/listar-venda")
    @RolesAllowed({"comprador", "funcionario"})
    public Response listarAVenda() {
        return ResponseForwarder.forward(veiculosClient.listarAVenda());
    }

    @GET
    @Path("/listar-vendidos")
    @RolesAllowed("funcionario")
    public Response listarVendidos() {
        return ResponseForwarder.forward(veiculosClient.listarVendidos());
    }

    @PUT
    @Path("/comprar/{placa}")
    @RolesAllowed("comprador")
    public Response comprar(@PathParam("placa") String placa) {
        return ResponseForwarder.forward(veiculosClient.comprar(placa, cpf()));
    }

    private String cpf() {
        return identity.getPrincipal().getName();
    }
}
