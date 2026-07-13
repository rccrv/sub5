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
public class ListarVeiculosAVendaEndpointTest {

    @Inject
    VeiculoRepository veiculoRepository;

    @Inject
    TestUtils testUtils;

    @Test
    void testListar() {
        String accessToken = testUtils.getAccessToken("73985377359");

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

        var resp = RestAssured.given()
            .auth()
            .oauth2(accessToken)
            .when()
            .get("/listar-venda")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .getList(".", VeiculoRespDto.class);

        var veiculosJpa = veiculoRepository.listarVeiculosAVenda();

        Assertions.assertNotNull(resp);
        Assertions.assertEquals(3, resp.size());
        Assertions.assertEquals(0, veiculosJpa.getFirst().getValor().compareTo(resp.getFirst().valor()));
        Assertions.assertEquals(0, veiculosJpa.getLast().getValor().compareTo(resp.getLast().valor()));

        veiculoRepository.delete(veiculo1Jpa);
        veiculoRepository.delete(veiculo2Jpa);
        veiculoRepository.delete(veiculo3Jpa);
    }
}
