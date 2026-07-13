package br.nom.rccrv.code.entpoint.comprador;

import br.nom.rccrv.code.arch.adapter.veiculo.VeiculoInputAdapter;
import br.nom.rccrv.code.arch.adapter.veiculo.VeiculoOutputAdapter;
import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.VeiculoReqDto;
import br.nom.rccrv.code.domain.dto.VeiculoRespDto;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;
import br.nom.rccrv.code.utils.TestUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

@QuarkusTest
@QuarkusTestResource(TestcontainerManager.class)
public class ComprarVeiculoEndpointTest {

    @Inject
    VeiculoRepository veiculoRepository;

    @Inject
    TestUtils testUtils;

    @Test
    void testComprar() {
        var username = "73985377359";
        String accessToken = testUtils.getAccessToken("73985377359");

        var veiculo1 = new VeiculoReqDto(
            "Gurgel",
            "BR-800",
            1990,
            "ABC1D23",
            "Prata",
            BigDecimal.valueOf(25000.00)
        );

        veiculoRepository.insert(
            VeiculoOutputAdapter.paraJpa(
                VeiculoInputAdapter.deReqDto(veiculo1)
            )
        );

        var resp = RestAssured.given()
            .auth()
            .oauth2(accessToken)
            .pathParam("placa", veiculo1.placa())
            .when()
            .put("/comprar/{placa}")
            .then()
            .statusCode(200)
            .extract()
            .as(VeiculoRespDto.class);

        var veiculoJpa = veiculoRepository.findById(resp.id()).orElseThrow();

        Assertions.assertNotNull(resp);
        Assertions.assertEquals(username, veiculoJpa.getCompradorCpf());
        Assertions.assertTrue(veiculoJpa.getVendido());

        veiculoRepository.delete(veiculoJpa);
    }
}
