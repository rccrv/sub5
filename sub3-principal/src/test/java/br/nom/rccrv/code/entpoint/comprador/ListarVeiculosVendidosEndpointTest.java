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
public class ListarVeiculosVendidosEndpointTest {

    @Inject
    VeiculoRepository veiculoRepository;

    @Inject
    TestUtils testUtils;

    @Test
    void testListar() {
        String accessTokenComprador = testUtils.getAccessToken("73985377359");

        var veiculo1 = new VeiculoReqDto(
            "Gurgel",
            "BR-800",
            1990,
            "ABC1D23",
            "Prata",
            BigDecimal.valueOf(25000.00)
        );

        var veiculo2 = new VeiculoReqDto(
            "Toyota",
            "Corolla",
            2015,
            "ABC1D25",
            "Azul Metálico",
            BigDecimal.valueOf(75000.00)
        );

        var veiculo3 = new VeiculoReqDto(
            "Volkswagen",
            "Golf",
            1995,
            "ABC1D24",
            "Preto",
            BigDecimal.valueOf(35000.00)
        );

        var veiculo1Jpa = veiculoRepository.insert(
            VeiculoOutputAdapter.paraJpa(
                VeiculoInputAdapter.deReqDto(veiculo1)
            )
        );

        var veiculo2Jpa = veiculoRepository.insert(
            VeiculoOutputAdapter.paraJpa(
                VeiculoInputAdapter.deReqDto(veiculo2)
            )
        );

        var veiculo3Jpa = veiculoRepository.insert(
            VeiculoOutputAdapter.paraJpa(
                VeiculoInputAdapter.deReqDto(veiculo3)
            )
        );

        var resp1 = RestAssured.given()
            .auth()
            .oauth2(accessTokenComprador)
            .pathParam("placa", veiculo1.placa())
            .when()
            .put("/comprar/{placa}")
            .then()
            .statusCode(200);

        var resp2 = RestAssured.given()
            .auth()
            .oauth2(accessTokenComprador)
            .pathParam("placa", veiculo2.placa())
            .when()
            .put("/comprar/{placa}")
            .then()
            .statusCode(200);

        var resp3 = RestAssured.given()
            .auth()
            .oauth2(accessTokenComprador)
            .pathParam("placa", veiculo3.placa())
            .when()
            .put("/comprar/{placa}")
            .then()
            .statusCode(200);

        String accessTokenFuncionario = testUtils.getAccessToken("funcionario");

        var resp = RestAssured.given()
            .auth()
            .oauth2(accessTokenFuncionario)
            .when()
            .get("/listar-vendidos")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .getList(".", VeiculoRespDto.class);

        var veiculosJpa = veiculoRepository.listarVeiculosVendidos();

        Assertions.assertNotNull(resp);
        Assertions.assertEquals(3, resp.size());
        Assertions.assertEquals(0, veiculosJpa.getFirst().getValor().compareTo(resp.getFirst().valor()));
        Assertions.assertEquals(0, veiculosJpa.getLast().getValor().compareTo(resp.getLast().valor()));

        veiculoRepository.delete(veiculo1Jpa);
        veiculoRepository.delete(veiculo2Jpa);
        veiculoRepository.delete(veiculo3Jpa);
    }
}
