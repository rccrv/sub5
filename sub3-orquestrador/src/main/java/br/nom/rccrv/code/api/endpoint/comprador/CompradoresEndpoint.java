package br.nom.rccrv.code.api.endpoint.comprador;

import br.nom.rccrv.code.api.client.CompradoresRestClient;
import br.nom.rccrv.code.api.endpoint.ResponseForwarder;
import br.nom.rccrv.code.domain.dto.rest.CompradorReqDto;
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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@RequestScoped
@Path("/compradores")
public class CompradoresEndpoint {

    private final CompradoresRestClient compradoresClient;

    @Inject
    public CompradoresEndpoint(@RestClient CompradoresRestClient compradoresClient) {
        this.compradoresClient = compradoresClient;
    }

    @POST
    @Path("/cadastrar")
    public Response cadastrar(@Valid CompradorReqDto request) {
        return ResponseForwarder.forward(compradoresClient.cadastrar(request));
    }

    @PUT
    @Path("/autorizar/{cpf}")
    @RolesAllowed("funcionario")
    public Response autorizar(
        @Parameter(description = "CPF do comprador", required = true)
        @PathParam("cpf") String cpf
    ) {
        return ResponseForwarder.forward(compradoresClient.autorizar(cpf));
    }

    @GET
    @Path("/listar")
    @RolesAllowed("funcionario")
    public Response listar() {
        return ResponseForwarder.forward(compradoresClient.listar());
    }
}
