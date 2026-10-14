package br.nom.rccrv.code.api.endpoint;

import br.nom.rccrv.code.arch.port.service.CreateUserAuthServicePort;
import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.CompradorReqDto;
import br.nom.rccrv.code.infrastructure.persistence.repository.CompradorRepository;
import br.nom.rccrv.code.utils.TestUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@QuarkusTest
@QuarkusTestResource(TestcontainerManager.class)
class CompradoresEndpointTest {

    private static final String CPF = "82276542071";

    @Inject
    CompradorRepository repository;

    @Inject
    CreateUserAuthServicePort authService;

    @Inject
    TestUtils testUtils;

    @AfterEach
    void limparBanco() {
        try (var entities = repository.findAll()) {
            repository.deleteAll(entities.toList());
        }
        authService.rollbackCriarComprador(CPF);
    }

    @Test
    void testCadastrarListarEAutorizarCompradorComContaKeycloak() {
        long id = RestAssured.given()
            .contentType("application/json")
            .body(new CompradorReqDto(
                "822.765.420-71",
                "Comprador",
                "Teste",
                "comprador@example.com",
                "11912345678"
            ))
            .when()
            .post("/cadastrar")
            .then()
            .statusCode(201)
            .body("cpf", Matchers.is(CPF))
            .body("autorizado", Matchers.is(false))
            .extract()
            .jsonPath()
            .getLong("id");

        Assertions.assertEquals(CPF, repository.findById(id)
            .orElseThrow()
            .getCpf());
        String token = testUtils.getAccessToken("funcionario");

        RestAssured.given()
            .auth()
            .oauth2(token)
            .when()
            .get("/listar")
            .then()
            .statusCode(200)
            .body("cpf", Matchers.contains(CPF));

        RestAssured.given()
            .auth()
            .oauth2(token)
            .when()
            .put("/autorizar/" + CPF)
            .then()
            .statusCode(200)
            .body("autorizado", Matchers.is(true));

        Assertions.assertTrue(repository.findById(id)
            .orElseThrow()
            .getAutorizado());

        Assertions.assertNotNull(testUtils.getAccessToken(CPF));

        RestAssured.given()
            .auth()
            .oauth2(token)
            .when()
            .get("/listar")
            .then()
            .statusCode(200)
            .body("size()", Matchers.is(0));
    }

    @Test
    void testListarSemCompradoresEAutorizarCompradorNaoEncontrado() {
        RestAssured.given()
            .when()
            .get("/listar")
            .then()
            .statusCode(200)
            .body("size()", Matchers.is(0));

        RestAssured.given()
            .when()
            .put("/autorizar/" + CPF)
            .then()
            .statusCode(404);
    }

    @Test
    void testCadastroDeCompradorInvalido() {
        RestAssured.given()
            .contentType("application/json")
            // O construtor do DTO rejeita este CPF antes de enviarmos a requisição HTTP.
            .body("""
                {
                    "cpf": "12345678910",
                    "primeiroNome": "Comprador",
                    "ultimoNome": "Teste",
                    "email": "teste@example.com",
                    "telefone": "11912345678"
                }
                """)
            .when()
            .post("/cadastrar")
            .then()
            .statusCode(400);
        try (var entities = repository.findAll()) {
            Assertions.assertEquals(0, entities.count());
        }
    }
}
