package br.nom.rccrv.code.api.client;

import br.nom.rccrv.code.domain.dto.rest.CompradorReqDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(configKey = "compradores")
public interface CompradoresRestClient {

    @POST
    @Path("/cadastrar")
    Response cadastrar(CompradorReqDto comprador);

    @PUT
    @Path("/autorizar/{cpf}")
    Response autorizar(@PathParam("cpf") String cpf);

    @GET
    @Path("/listar")
    Response listar();
}
