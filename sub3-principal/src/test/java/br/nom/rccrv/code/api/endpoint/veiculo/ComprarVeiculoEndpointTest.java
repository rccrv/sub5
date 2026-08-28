package br.nom.rccrv.code.api.endpoint.veiculo;

import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.VeiculoReqDto;
import br.nom.rccrv.code.domain.dto.VeiculoRespDto;
import br.nom.rccrv.code.domain.mapper.VeiculoInputMapper;
import br.nom.rccrv.code.domain.mapper.VeiculoOutputMapper;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;
import br.nom.rccrv.code.infrastructure.persistence.entity.Veiculo;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

@QuarkusTest
@QuarkusTestResource(TestcontainerManager.class)
public class ComprarVeiculoEndpointTest {

    @Inject
    VeiculoRepository veiculoRepository;

    @Test
    void testComprar() {
        var username = "73985377359";

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

        var resp = RestAssured.given()
            .header("X-CPF", username)
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

    @Test
    void testComprarSemCpf() {
        RestAssured.given()
            .pathParam("placa", "ABC1D23")
            .when()
            .put("/comprar/{placa}")
            .then()
            .statusCode(400);
    }

    @Test
    void testComprarComPagamento() {
        var cpf = "73985377359";
        var pagamentoId = UUID.randomUUID();
        var request = new VeiculoReqDto(
            "Gurgel",
            "BR-800",
            1990,
            "ABC1D23",
            "Prata",
            BigDecimal.valueOf(25000.00)
        );
        var persisted = insert(request);

        var response = RestAssured.given()
            .header("X-CPF", cpf)
            .header("X-Pagamento-Id", pagamentoId)
            .pathParam("placa", request.placa())
            .when()
            .put("/comprar/{placa}")
            .then()
            .statusCode(200)
            .extract()
            .as(VeiculoRespDto.class);

        var vehicle = veiculoRepository.findById(response.id()).orElseThrow();

        Assertions.assertEquals(cpf, vehicle.getCompradorCpf());
        Assertions.assertEquals(pagamentoId, vehicle.getPagamentoId());
        Assertions.assertTrue(vehicle.getVendido());

        veiculoRepository.delete(persisted);
    }

    @Test
    void testSomenteUmCompradorPodeComprar() {
        var request = new VeiculoReqDto(
            "Gurgel",
            "BR-800",
            1990,
            "ABC1D23",
            "Prata",
            BigDecimal.valueOf(25000.00)
        );
        var persisted = insert(request);

        RestAssured.given()
            .header("X-CPF", "73985377359")
            .header("X-Pagamento-Id", UUID.randomUUID())
            .pathParam("placa", request.placa())
            .when()
            .put("/comprar/{placa}")
            .then()
            .statusCode(200);

        RestAssured.given()
            .header("X-CPF", "82276542071")
            .header("X-Pagamento-Id", UUID.randomUUID())
            .pathParam("placa", request.placa())
            .when()
            .put("/comprar/{placa}")
            .then()
            .statusCode(404);

        veiculoRepository.delete(persisted);
    }

    @Test
    void testRollbackComPagamentoCorrespondente() {
        var request = new VeiculoReqDto(
            "Gurgel",
            "BR-800",
            1990,
            "ABC1D23",
            "Prata",
            BigDecimal.valueOf(25000.00)
        );
        var pagamentoId = UUID.randomUUID();
        var persisted = insert(request);

        RestAssured.given()
            .header("X-CPF", "73985377359")
            .header("X-Pagamento-Id", pagamentoId)
            .pathParam("placa", request.placa())
            .when()
            .put("/comprar/{placa}")
            .then()
            .statusCode(200);

        RestAssured.given()
            .header("X-Pagamento-Id", pagamentoId)
            .pathParam("placa", request.placa())
            .when()
            .put("/interno/veiculos/{placa}/rollback")
            .then()
            .statusCode(204);

        var vehicle = veiculoRepository.findById(persisted.getId()).orElseThrow();

        Assertions.assertFalse(vehicle.getVendido());
        Assertions.assertNull(vehicle.getCompradorCpf());
        Assertions.assertNull(vehicle.getPagamentoId());

        veiculoRepository.delete(vehicle);
    }

    @Test
    void testRollbackNaoPodeAtingirOutroPagamento() {
        var request = new VeiculoReqDto(
            "Gurgel",
            "BR-800",
            1990,
            "ABC1D23",
            "Prata",
            BigDecimal.valueOf(25000.00)
        );
        var pagamentoId = UUID.randomUUID();
        var persisted = insert(request);

        RestAssured.given()
            .header("X-CPF", "73985377359")
            .header("X-Pagamento-Id", pagamentoId)
            .pathParam("placa", request.placa())
            .when()
            .put("/comprar/{placa}")
            .then()
            .statusCode(200);

        RestAssured.given()
            .header("X-Pagamento-Id", UUID.randomUUID())
            .pathParam("placa", request.placa())
            .when()
            .put("/interno/veiculos/{placa}/rollback")
            .then()
            .statusCode(404);

        var vehicle = veiculoRepository.findById(persisted.getId()).orElseThrow();

        Assertions.assertTrue(vehicle.getVendido());
        Assertions.assertEquals(pagamentoId, vehicle.getPagamentoId());

        veiculoRepository.delete(vehicle);
    }

    private Veiculo insert(VeiculoReqDto request) {
        return veiculoRepository.insert(
            VeiculoOutputMapper.paraJpa(VeiculoInputMapper.deReqDto(request))
        );
    }
}
