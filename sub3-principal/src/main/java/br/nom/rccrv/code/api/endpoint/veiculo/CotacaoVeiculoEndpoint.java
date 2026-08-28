package br.nom.rccrv.code.api.endpoint.veiculo;

import br.nom.rccrv.code.arch.controller.VeiculoController;
import br.nom.rccrv.code.domain.dto.CotacaoVeiculoRespDto;
import br.nom.rccrv.code.infrastructure.persistence.adapter.VeiculoRepositoryAdapter;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@RequestScoped
@Path("/interno/veiculos")
public class CotacaoVeiculoEndpoint {

  private final VeiculoController controller;

  @Inject
  public CotacaoVeiculoEndpoint(VeiculoRepositoryAdapter repository) {
    controller = new VeiculoController(repository);
  }

  @GET
  @Path("/{placa}/cotacao")
  public CotacaoVeiculoRespDto cotar(
      @PathParam("placa")
      @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z][0-9]{2}$") String placa) {
    return controller
        .listarVeiculosAVenda()
        .stream()
        .filter(veiculo -> veiculo.getPlaca().equals(placa))
        .findFirst()
        .map(veiculo -> new CotacaoVeiculoRespDto(veiculo.getPlaca(), veiculo.getValor()))
        .orElseThrow(() -> new NotFoundException("Veículo indisponível"));
  }
}
