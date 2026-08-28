package br.nom.rccrv.code.api.endpoint.veiculo;

import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.VeiculoReqDto;
import br.nom.rccrv.code.domain.dto.VeiculoRespDto;
import br.nom.rccrv.code.domain.mapper.VeiculoInputMapper;
import br.nom.rccrv.code.domain.mapper.VeiculoOutputMapper;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

@QuarkusTest
@QuarkusTestResource(TestcontainerManager.class)
public class EditarVeiculoEndpointTest {

    @Inject
    VeiculoRepository veiculoRepository;

    @Test
    void testEditar() {
        var veiculo1 = new VeiculoReqDto(
            "Gurgel",
            "BR-800",
            1990,
            "ABC1D23",
            "Prata",
            BigDecimal.valueOf(25000.00)
        );

        veiculoRepository.insert(
            VeiculoOutputMapper.paraJpa(
                VeiculoInputMapper.deReqDto(veiculo1)
            )
        );

        var req = new VeiculoReqDto(
            "Gurgel",
            "BR-800",
            1991,
            "ABC1D23",
            "Prata",
            BigDecimal.valueOf(26000.00)
        );

        var resp = RestAssured.given()
            .pathParam("placa", veiculo1.placa())
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .when()
            .put("/editar/{placa}")
            .then()
            .statusCode(200)
            .extract()
            .as(VeiculoRespDto.class);

        var veiculoJpa = veiculoRepository.findById(resp.id()).orElseThrow();

        Assertions.assertNotNull(resp);
        Assertions.assertEquals(req.placa(), veiculoJpa.getPlaca());
        Assertions.assertEquals(req.ano(), veiculoJpa.getAno());
        Assertions.assertEquals(0, req.valor().compareTo(veiculoJpa.getValor()));

        veiculoRepository.delete(veiculoJpa);
    }
}
