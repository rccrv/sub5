package br.nom.rccrv.code.api.endpoint;

import br.nom.rccrv.code.api.client.CompradoresRestClient;
import br.nom.rccrv.code.api.client.VeiculosRestClient;
import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.rest.CompradorReqDto;
import br.nom.rccrv.code.domain.dto.rest.VeiculoReqDto;
import br.nom.rccrv.code.utils.TestUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@QuarkusTest
@QuarkusTestResource(TestcontainerManager.class)
class BffEndpointsTest {

    private static final String JSON = MediaType.APPLICATION_JSON;

    @InjectMock
    @RestClient
    CompradoresRestClient compradoresClient;

    @InjectMock
    @RestClient
    VeiculosRestClient veiculosClient;

    @Inject
    TestUtils testUtils;

    @AfterEach
    void resetMocks() {
        Mockito.reset(compradoresClient, veiculosClient);
    }

    @Test
    void cadastrarCompradorEncaminhaRequestEResposta() {
        var req = new CompradorReqDto("822.765.420-71", "Ana", "Silva", "ana@example.com", "11912345678");
        Mockito.when(compradoresClient.cadastrar(ArgumentMatchers.any())).thenReturn(Response.status(201).type(JSON).entity("{\"cpf\":\"82276542071\"}").build());

        var response = RestAssured.given().contentType(JSON).body(req).post("/compradores/cadastrar");

        Assertions.assertEquals(201, response.statusCode());
        Mockito.verify(compradoresClient).cadastrar(ArgumentMatchers.eq(req));
    }

    @Test
    void autorizarCompradorEncaminhaCpf() {
        Mockito.when(compradoresClient.autorizar("82276542071")).thenReturn(Response.ok().type(JSON).entity("{} ").build());

        var response = RestAssured.given()
            .auth().oauth2(testUtils.getAccessToken("funcionario"))
            .put("/compradores/autorizar/82276542071");

        Assertions.assertEquals(200, response.statusCode());
        Mockito.verify(compradoresClient).autorizar("82276542071");
    }

    @Test
    void listarCompradoresEncaminhaResposta() {
        Mockito.when(compradoresClient.listar()).thenReturn(Response.ok().type(JSON).entity("[]").build());

        var response = RestAssured.given()
            .auth().oauth2(testUtils.getAccessToken("funcionario"))
            .get("/compradores/listar");

        Assertions.assertEquals(200, response.statusCode());
        Mockito.verify(compradoresClient).listar();
    }

    @Test
    void cadastrarVeiculoEncaminhaRequest() {
        var req = veiculoReq();
        Mockito.when(veiculosClient.cadastrar(ArgumentMatchers.any())).thenReturn(Response.status(201).type(JSON).entity("{}").build());

        var response = RestAssured.given()
            .auth().oauth2(testUtils.getAccessToken("funcionario"))
            .contentType(JSON).body(req)
            .post("/veiculos/cadastrar");

        Assertions.assertEquals(201, response.statusCode());
        Mockito.verify(veiculosClient).cadastrar(ArgumentMatchers.eq(req));
    }

    @Test
    void editarVeiculoEncaminhaPlacaERequest() {
        var req = veiculoReq();
        Mockito.when(veiculosClient.editar("ABC1D23", req)).thenReturn(Response.ok().type(JSON).entity("{}").build());

        var response = RestAssured.given()
            .auth().oauth2(testUtils.getAccessToken("funcionario"))
            .contentType(JSON).body(req)
            .put("/veiculos/editar/ABC1D23");

        Assertions.assertEquals(200, response.statusCode());
        Mockito.verify(veiculosClient).editar("ABC1D23", req);
    }

    @Test
    void listarVeiculosAVendaEncaminhaRequest() {
        Mockito.when(veiculosClient.listarAVenda()).thenReturn(Response.ok().type(JSON).entity("[]").build());

        var response = RestAssured.given()
            .auth().oauth2(testUtils.getAccessToken("comprador1"))
            .get("/veiculos/listar-venda");

        Assertions.assertEquals(200, response.statusCode());
        Mockito.verify(veiculosClient).listarAVenda();
    }

    @Test
    void listarVeiculosVendidosEncaminhaRequest() {
        Mockito.when(veiculosClient.listarVendidos()).thenReturn(Response.ok().type(JSON).entity("[]").build());

        var response = RestAssured.given()
            .auth().oauth2(testUtils.getAccessToken("funcionario"))
            .get("/veiculos/listar-vendidos");

        Assertions.assertEquals(200, response.statusCode());
        Mockito.verify(veiculosClient).listarVendidos();
    }

    @Test
    void comprarVeiculoEncaminhaPlacaECpf() {
        var cpf = "73985377359";
        Mockito.when(veiculosClient.comprar("ABC1D23", cpf))
            .thenReturn(Response.ok().type(JSON).entity("{}").build());

        var response = RestAssured.given()
            .auth().oauth2(testUtils.getAccessToken(cpf))
            .put("/veiculos/comprar/ABC1D23");

        Assertions.assertEquals(200, response.statusCode());
        Mockito.verify(veiculosClient).comprar("ABC1D23", cpf);
    }

    @Test
    void rotasProtegidasRejeitamRequestSemToken() {
        RestAssured.given().get("/compradores/listar").then().statusCode(401);
        RestAssured.given().get("/veiculos/listar-vendidos").then().statusCode(401);
    }

    private static VeiculoReqDto veiculoReq() {
        return new VeiculoReqDto("Gurgel", "BR-800", 1990, "ABC1D23", "Prata", BigDecimal.valueOf(25000));
    }
}
