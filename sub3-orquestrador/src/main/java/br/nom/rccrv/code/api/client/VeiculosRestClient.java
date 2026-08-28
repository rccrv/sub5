package br.nom.rccrv.code.api.client;

import br.nom.rccrv.code.domain.dto.rest.CotacaoVeiculoRespDto;
import br.nom.rccrv.code.domain.dto.rest.VeiculoReqDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(configKey = "veiculos")
public interface VeiculosRestClient {

  @POST
  @Path("/cadastrar")
  Response cadastrar(VeiculoReqDto veiculo);

  @PUT
  @Path("/editar/{placa}")
  Response editar(@PathParam("placa") String placa, VeiculoReqDto veiculo);

  @GET
  @Path("/listar-venda")
  Response listarAVenda();

  @GET
  @Path("/listar-vendidos")
  Response listarVendidos();

  @GET
  @Path("/interno/veiculos/{placa}/cotacao")
  CotacaoVeiculoRespDto cotar(@PathParam("placa") String placa);

  @PUT
  @Path("/comprar/{placa}")
  Response comprar(
      @PathParam("placa") String placa,
      @HeaderParam("X-CPF") String cpf,
      @HeaderParam("X-Pagamento-Id") UUID pagamentoId);

  default Response comprar(String placa, String cpf) {
    return comprar(placa, cpf, null);
  }

  @PUT
  @Path("/interno/veiculos/{placa}/rollback")
  Response rollback(
      @PathParam("placa") String placa,
      @HeaderParam("X-Pagamento-Id") UUID pagamentoId);
}
