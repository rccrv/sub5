package br.nom.rccrv.code.api.endpoint.veiculo;

import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.VeiculoReqDto;
import br.nom.rccrv.code.domain.dto.VeiculoRespDto;
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
public class CadastrarVeiculoEndpointTest {

    @Inject
    VeiculoRepository veiculoRepository;

    @Test
    void testCadastroValido() {

        var req = new VeiculoReqDto(
            "Gurgel",
            "BR-800",
            1990,
            "ABC1D23",
            "Prata",
            BigDecimal.valueOf(25000.00)
        );

        var resp = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .when()
            .post("/cadastrar")
            .then()
            .statusCode(201)
            .extract()
            .as(VeiculoRespDto.class);

        var entityJpa = veiculoRepository.findById(resp.id()).orElseThrow();
        Assertions.assertNotNull(entityJpa);
        Assertions.assertEquals(req.placa(), entityJpa.getPlaca());

        veiculoRepository.delete(entityJpa);
    }
}
