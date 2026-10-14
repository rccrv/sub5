package br.nom.rccrv.code.api.endpoint;

import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.VeiculoReqDto;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

@QuarkusTest
@QuarkusTestResource(TestcontainerManager.class)
class VeiculosEndpointTest {

    @Inject
    VeiculoRepository repository;

    @AfterEach
    void limparBanco() {
        try (var entities = repository.findAll()) {
            repository.deleteAll(entities.toList());
        }
    }

    private VeiculoReqDto criarVeiculo(String placa, int valor) {
        return new VeiculoReqDto(
            "Fiat",
            "Uno",
            2020,
            placa,
            "Branco",
            BigDecimal.valueOf(valor)
        );
    }

    private long cadastrarVeiculo(String placa, int valor) {
        return RestAssured.given()
            .contentType("application/json")
            .body(criarVeiculo(placa, valor))
            .when()
            .post("/cadastrar")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");
    }

    @Test
    void testCadastrarEditarEListarVeiculosPorValor() {
        long id = cadastrarVeiculo("ABC1D23", 30000);
        cadastrarVeiculo("DEF4G56", 10000);

        Assertions.assertEquals("ABC1D23", repository.findById(id)
            .orElseThrow()
            .getPlaca());

        RestAssured.given()
            .when()
            .get("/listar-venda")
            .then()
            .statusCode(200)
            .body("placa", Matchers.contains("DEF4G56", "ABC1D23"));

        RestAssured.given()
            .when()
            .get("/listar-vendidos")
            .then()
            .statusCode(200)
            .body("size()", Matchers.is(0));
        double editedPrice = RestAssured.given()
            .contentType("application/json")
            .body(criarVeiculo("ABC1D23", 20000))
            .when()
            .put("/editar/ABC1D23")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getDouble("valor");
        double quotedPrice = RestAssured.given()
            .when()
            .get("/interno/veiculos/ABC1D23/cotacao")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getDouble("valor");

        Assertions.assertEquals(20000.0, editedPrice);

        Assertions.assertEquals(20000.0, quotedPrice);
    }

    @Test
    void testComprarERollbackSomenteDoPagamentoCorrespondente() {
        long id = cadastrarVeiculo("ABC1D23", 30000);
        UUID payment = UUID.randomUUID();

        RestAssured.given()
            .header("X-CPF", "73985377359")
            .header("X-Pagamento-Id", payment)
            .when()
            .put("/comprar/ABC1D23")
            .then()
            .statusCode(200)
            .body("vendido", Matchers.is(true));

        Assertions.assertEquals(payment, repository.findById(id)
            .orElseThrow()
            .getPagamentoId());

        RestAssured.given()
            .when()
            .get("/listar-venda")
            .then()
            .statusCode(200)
            .body("size()", Matchers.is(0));

        RestAssured.given()
            .when()
            .get("/listar-vendidos")
            .then()
            .statusCode(200)
            .body("placa", Matchers.contains("ABC1D23"));

        RestAssured.given()
            .when()
            .get("/interno/veiculos/ABC1D23/cotacao")
            .then()
            .statusCode(404);

        RestAssured.given()
            .header("X-Pagamento-Id", UUID.randomUUID())
            .when()
            .put("/interno/veiculos/ABC1D23/rollback")
            .then()
            .statusCode(404);

        Assertions.assertTrue(repository.findById(id)
            .orElseThrow()
            .getVendido());

        RestAssured.given()
            .header("X-Pagamento-Id", payment)
            .when()
            .put("/interno/veiculos/ABC1D23/rollback")
            .then()
            .statusCode(204);

        Assertions.assertFalse(repository.findById(id)
            .orElseThrow()
            .getVendido());

        Assertions.assertNull(repository.findById(id)
            .orElseThrow()
            .getPagamentoId());

        Assertions.assertEquals("", repository.findById(id)
            .orElseThrow()
            .getCompradorCpf());
    }

    @Test
    void testRequisicoesInvalidasEVeiculosNaoEncontrados() {
        RestAssured.given()
            .contentType("application/json")
            .body(criarVeiculo("invalid", 100))
            .when()
            .post("/cadastrar")
            .then()
            .statusCode(400);

        RestAssured.given()
            .contentType("application/json")
            .body(criarVeiculo("ABC1D23", -1))
            .when()
            .post("/cadastrar")
            .then()
            .statusCode(400);

        RestAssured.given()
            .contentType("application/json")
            .body(criarVeiculo("ABC1D23", 100))
            .when()
            .put("/editar/ABC1D23")
            .then()
            .statusCode(404);

        RestAssured.given()
            .header("X-CPF", "73985377359")
            .when()
            .put("/comprar/ABC1D23")
            .then()
            .statusCode(404);

        RestAssured.given()
            .when()
            .get("/interno/veiculos/ABC1D23/cotacao")
            .then()
            .statusCode(404);

        RestAssured.given()
            .when()
            .put("/interno/veiculos/ABC1D23/rollback")
            .then()
            .statusCode(400);
    }
}
