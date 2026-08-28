package br.nom.rccrv.code.api.endpoint.veiculo;

import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.CotacaoVeiculoRespDto;
import br.nom.rccrv.code.domain.dto.VeiculoReqDto;
import br.nom.rccrv.code.domain.mapper.VeiculoInputMapper;
import br.nom.rccrv.code.domain.mapper.VeiculoOutputMapper;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(TestcontainerManager.class)
public class CotacaoVeiculoEndpointTest {

    @Inject
    VeiculoRepository veiculoRepository;

    @Test
    void testCotarVeiculoDisponivel() {
        var request = new VeiculoReqDto(
            "Gurgel",
            "BR-800",
            1990,
            "ABC1D23",
            "Prata",
            BigDecimal.valueOf(25000.00)
        );
        var vehicle = veiculoRepository.insert(
            VeiculoOutputMapper.paraJpa(VeiculoInputMapper.deReqDto(request))
        );

        var quote = RestAssured.given()
            .pathParam("placa", request.placa())
            .when()
            .get("/interno/veiculos/{placa}/cotacao")
            .then()
            .statusCode(200)
            .extract()
            .as(CotacaoVeiculoRespDto.class);

        Assertions.assertEquals(request.placa(), quote.placa());
        Assertions.assertEquals(0, request.valor().compareTo(quote.valor()));

        veiculoRepository.delete(vehicle);
    }

    @Test
    void testNaoCotarVeiculoVendido() {
        var request = new VeiculoReqDto(
            "Gurgel",
            "BR-800",
            1990,
            "ABC1D23",
            "Prata",
            BigDecimal.valueOf(25000.00)
        );
        var vehicle = veiculoRepository.insert(
            VeiculoOutputMapper.paraJpa(VeiculoInputMapper.deReqDto(request))
        );

        RestAssured.given()
            .header("X-CPF", "73985377359")
            .header("X-Pagamento-Id", UUID.randomUUID())
            .pathParam("placa", request.placa())
            .when()
            .put("/comprar/{placa}")
            .then()
            .statusCode(200);

        RestAssured.given()
            .pathParam("placa", request.placa())
            .when()
            .get("/interno/veiculos/{placa}/cotacao")
            .then()
            .statusCode(404);

        veiculoRepository.delete(vehicle);
    }
}
