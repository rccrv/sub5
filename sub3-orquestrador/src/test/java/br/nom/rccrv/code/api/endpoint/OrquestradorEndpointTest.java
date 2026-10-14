package br.nom.rccrv.code.api.endpoint;

import br.nom.rccrv.code.api.client.CompradoresRestClient;
import br.nom.rccrv.code.api.client.FinanceiroRestClient;
import br.nom.rccrv.code.api.client.VeiculosRestClient;
import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.rest.CotacaoVeiculoRespDto;
import br.nom.rccrv.code.domain.dto.rest.CompradorReqDto;
import br.nom.rccrv.code.domain.dto.rest.PagamentoDtos;
import br.nom.rccrv.code.domain.dto.rest.VeiculoReqDto;
import br.nom.rccrv.code.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.restassured.RestAssured;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@QuarkusTest
@QuarkusTestResource(TestcontainerManager.class)
class OrquestradorEndpointTest {

    private static final String CPF = "73985377359";

    @InjectMock
    @RestClient
    CompradoresRestClient compradores;

    @InjectMock
    @RestClient
    VeiculosRestClient veiculos;

    @InjectMock
    @RestClient
    FinanceiroRestClient financeiro;

    @Inject
    TestUtils tokens;

    @Inject
    ObjectMapper mapper;

    @InjectKafkaCompanion
    KafkaCompanion companion;

    @BeforeEach
    void reiniciarMocks() {
        Mockito.reset(compradores, veiculos, financeiro);
    }

    // ResponseForwarder reads an inbound HTTP response, rather than an outbound builder response.
    private Response criarResposta(int status, String body) {
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.getStatus())
            .thenReturn(status);
        Mockito.when(response.hasEntity())
            .thenReturn(body != null);
        Mockito.when(response.readEntity(String.class))
            .thenReturn(body);
        Mockito.when(response.getStatusInfo())
            .thenReturn(Response.Status.fromStatusCode(status));
        return response;
    }

    private VeiculoReqDto criarVeiculo() {
        return new VeiculoReqDto(
            "Fiat",
            "Uno",
            2020,
            "ABC1D23",
            "Branco",
            new BigDecimal("30000")
        );
    }

    @Test
    void testAutorizacaoDeTodasAsRotasProtegidas() {
        String employee = tokens.getAccessToken("funcionario");
        String buyer = tokens.getAccessToken(CPF);
        for (String path : new String[]{"/compradores/listar", "/veiculos/listar-vendidos"}) {
            RestAssured.given()
                .when()
                .get(path)
                .then()
                .statusCode(401);

            RestAssured.given()
                .auth()
                .oauth2(buyer)
                .when()
                .get(path)
                .then()
                .statusCode(403);
        }
        RestAssured.given()
            .when()
            .get("/veiculos/listar-venda")
            .then()
            .statusCode(401);

        RestAssured.given()
            .when()
            .put("/compradores/autorizar/82276542071")
            .then()
            .statusCode(401);

        RestAssured.given()
            .auth()
            .oauth2(buyer)
            .when()
            .put("/compradores/autorizar/82276542071")
            .then()
            .statusCode(403);

        RestAssured.given()
            .contentType("application/json")
            .body(criarVeiculo())
            .when()
            .post("/veiculos/cadastrar")
            .then()
            .statusCode(401);

        RestAssured.given()
            .auth()
            .oauth2(buyer)
            .contentType("application/json")
            .body(criarVeiculo())
            .when()
            .post("/veiculos/cadastrar")
            .then()
            .statusCode(403);

        RestAssured.given()
            .contentType("application/json")
            .body(criarVeiculo())
            .when()
            .put("/veiculos/editar/ABC1D23")
            .then()
            .statusCode(401);

        RestAssured.given()
            .auth()
            .oauth2(buyer)
            .contentType("application/json")
            .body(criarVeiculo())
            .when()
            .put("/veiculos/editar/ABC1D23")
            .then()
            .statusCode(403);

        RestAssured.given()
            .when()
            .put("/veiculos/comprar/ABC1D23")
            .then()
            .statusCode(401);

        RestAssured.given()
            .auth()
            .oauth2(employee)
            .when()
            .put("/veiculos/comprar/ABC1D23")
            .then()
            .statusCode(403);
        UUID id = UUID.randomUUID();

        RestAssured.given()
            .when()
            .get("/pagamentos/" + id)
            .then()
            .statusCode(401);

        RestAssured.given()
            .auth()
            .oauth2(employee)
            .when()
            .get("/pagamentos/" + id)
            .then()
            .statusCode(403);
        for (String path : new String[]{"/pagamentos/reservas", "/pagamentos/" + id + "/pagar"}) {
            Object req = path.endsWith("/reservas")
                ? new PagamentoDtos.CriarReservaReq(null, null, null)
                : new PagamentoDtos.PagarReq(null);
            RestAssured.given()
                .contentType("application/json")
                .body(req)
                .when()
                .post(path)
                .then()
                .statusCode(401);

            RestAssured.given()
                .auth()
                .oauth2(employee)
                .contentType("application/json")
                .body(req)
                .when()
                .post(path)
                .then()
                .statusCode(403);
        }
        Mockito.verifyNoInteractions(compradores, veiculos, financeiro);
    }

    @Test
    void testEncaminharRequisicoesDeCompradoresEErrosDosServicos() {
        Mockito.when(compradores.cadastrar(Mockito.any()))
            .thenAnswer(inv -> criarResposta(201, "{\"cpf\":\"82276542071\"}"));
        Mockito.when(compradores.listar())
            .thenAnswer(inv -> criarResposta(200, "[]"));
        Mockito.when(compradores.autorizar("82276542071"))
            .thenAnswer(inv -> criarResposta(404, "{\"error\":\"missing\"}"));

        RestAssured.given()
            .contentType("application/json")
            .body(new CompradorReqDto(
                "82276542071",
                "Teste",
                "Comprador",
                "teste@example.com",
                "11912345678"
            ))
            .when()
            .post("/compradores/cadastrar")
            .then()
            .statusCode(201)
            .body("cpf", Matchers.is("82276542071"));
        String token = tokens.getAccessToken("funcionario");

        RestAssured.given()
            .auth()
            .oauth2(token)
            .when()
            .get("/compradores/listar")
            .then()
            .statusCode(200)
            .body("size()", Matchers.is(0));

        RestAssured.given()
            .auth()
            .oauth2(token)
            .when()
            .put("/compradores/autorizar/82276542071")
            .then()
            .statusCode(404)
            .body("error", Matchers.is("missing"));

        Mockito.verify(compradores)
            .cadastrar(Mockito.argThat(req -> req.cpf()
                .equals("82276542071")));
    }

    @Test
    void testEncaminharRequisicoesDeVeiculosComCpfDoCompradorAutenticado() {
        Mockito.when(veiculos.cadastrar(Mockito.any()))
            .thenAnswer(inv -> criarResposta(201, "{\"placa\":\"ABC1D23\"}"));
        Mockito.when(veiculos.editar(Mockito.eq("ABC1D23"), Mockito.any()))
            .thenAnswer(inv -> criarResposta(200, "{}"));
        Mockito.when(veiculos.listarAVenda())
            .thenAnswer(inv -> criarResposta(200, "[]"));
        Mockito.when(veiculos.listarVendidos())
            .thenAnswer(inv -> criarResposta(200, "[]"));
        Mockito.when(veiculos.comprar("ABC1D23", CPF, null))
            .thenAnswer(inv -> criarResposta(200, "{}"));
        String employee = tokens.getAccessToken("funcionario");
        String buyer = tokens.getAccessToken(CPF);

        RestAssured.given()
            .auth()
            .oauth2(employee)
            .contentType("application/json")
            .body(criarVeiculo())
            .when()
            .post("/veiculos/cadastrar")
            .then()
            .statusCode(201)
            .body("placa", Matchers.is("ABC1D23"));

        RestAssured.given()
            .auth()
            .oauth2(employee)
            .contentType("application/json")
            .body(criarVeiculo())
            .when()
            .put("/veiculos/editar/ABC1D23")
            .then()
            .statusCode(200);

        RestAssured.given()
            .auth()
            .oauth2(buyer)
            .when()
            .get("/veiculos/listar-venda")
            .then()
            .statusCode(200);

        RestAssured.given()
            .auth()
            .oauth2(employee)
            .when()
            .get("/veiculos/listar-vendidos")
            .then()
            .statusCode(200);

        RestAssured.given()
            .auth()
            .oauth2(buyer)
            .when()
            .put("/veiculos/comprar/ABC1D23")
            .then()
            .statusCode(200);

        Mockito.verify(veiculos)
            .comprar("ABC1D23", CPF, null);
    }

    @Test
    void testCotarReservaNoServidorEEncaminharConsultaDePagamento() {
        UUID id = UUID.randomUUID();
        Mockito.when(veiculos.cotar("ABC1D23"))
            .thenReturn(new CotacaoVeiculoRespDto("ABC1D23", new BigDecimal("30000")));
        Mockito.when(financeiro.reservar(Mockito.any()))
            .thenAnswer(inv -> criarResposta(201, "{}"));
        Mockito.when(financeiro.consultar(id, CPF))
            .thenAnswer(inv -> criarResposta(200, "{\"status\":\"PENDING\"}"));
        String buyer = tokens.getAccessToken(CPF);

        RestAssured.given()
            .auth()
            .oauth2(buyer)
            .contentType("application/json")
            .body(new PagamentoDtos.CriarReservaReq(
                "ABC1D23",
                "Rua Teste",
                "01001000"
            ))
            .when()
            .post("/pagamentos/reservas")
            .then()
            .statusCode(201);

        Mockito.verify(financeiro)
            .reservar(Mockito.argThat(req -> {
                boolean cpfCorreto = req.cpf()
                    .equals(CPF);
                boolean valorCorreto = req.quotedAmount()
                    .compareTo(new BigDecimal("30000")) == 0;
                return cpfCorreto && valorCorreto;
            }));

        RestAssured.given()
            .auth()
            .oauth2(buyer)
            .when()
            .get("/pagamentos/" + id)
            .then()
            .statusCode(200)
            .body("status", Matchers.is("PENDING"));
    }

    private UUID pagar() {
        UUID id = UUID.randomUUID();
        Mockito.when(financeiro.pagar(Mockito.eq(id), Mockito.eq(CPF), Mockito.any()))
            .thenAnswer(inv -> criarResposta(202, "{}"));

        RestAssured.given()
            .auth()
            .oauth2(tokens.getAccessToken(CPF))
            .contentType("application/json")
            .body(new PagamentoDtos.PagarReq("pix-test"))
            .when()
            .post("/pagamentos/" + id + "/pagar")
            .then()
            .statusCode(202);

        Mockito.verify(financeiro)
            .pagar(Mockito.eq(id), Mockito.eq(CPF), Mockito.argThat(req -> req.pixCode()
                .equals("pix-test")));
        return id;
    }

    private void enviarAck(UUID id, String status) throws Exception {
        String json = mapper.writeValueAsString(Map.of("transactionId", id, "status", status,
            "cpf", CPF, "placa", "ABC1D23", "type", "SagaAck"));
        companion.produceStrings()
            .fromRecords(new ProducerRecord<>("financeiro-ack", json))
            .awaitCompletion(Duration.ofSeconds(20));
    }

    @Test
    void testAckKafkaDeSucessoConcluiVendaDoVeiculo() throws Exception {
        UUID id = pagar();
        Mockito.when(veiculos.comprar("ABC1D23", CPF, id))
            .thenAnswer(inv -> criarResposta(200, "{}"));
        enviarAck(id, "SUCCESS");

        Mockito.verify(veiculos, Mockito.timeout(20000))
            .comprar("ABC1D23", CPF, id);

        Mockito.verify(veiculos, Mockito.never())
            .rollback(Mockito.anyString(), Mockito.any());
    }

    @Test
    void testFalhaDoPrincipalPublicaCompensacaoFinanceira() throws Exception {
        UUID id = pagar();
        Mockito.when(veiculos.comprar("ABC1D23", CPF, id))
            .thenAnswer(inv -> criarResposta(409, null));
        Mockito.when(veiculos.rollback("ABC1D23", id))
            .thenAnswer(inv -> criarResposta(204, null));
        try (var commands = companion.consumeStrings()
            .withGroupId(UUID.randomUUID()
                .toString())
            .withProp("auto.offset.reset", "earliest")
            .fromTopics(Set.of("financeiro"), records -> records
                .select()
                .where(record -> record.value()
                    .contains(id.toString()))
                .select()
                .first(1))) {
            enviarAck(id, "SUCCESS");
            commands.awaitCompletion(Duration.ofSeconds(20));
            var command = mapper.readTree(commands.getFirstRecord()
                .value());

            Assertions.assertEquals(id.toString(), command.get("pagamentoId")
                .asText());

            Assertions.assertEquals("ROLLBACK", command.get("stepType")
                .asText());

            Assertions.assertEquals("CancelaPagamentoDto", command.get("type")
                .asText());

            Mockito.verify(veiculos)
                .rollback("ABC1D23", id);
        }
    }

    @Test
    void testAckFinanceiroDeFalhaNaoVendeVeiculo() throws Exception {
        UUID id = pagar();
        enviarAck(id, "FAILED");
        // A second success for the same id must be ignored after the failure completes the saga.
        enviarAck(id, "SUCCESS");
        UUID nextPayment = pagar();
        Mockito.when(veiculos.comprar("ABC1D23", CPF, nextPayment))
            .thenAnswer(inv -> criarResposta(200, "{}"));
        // One partition preserves ordering: processing this acknowledgement proves the earlier ones were consumed.
        enviarAck(nextPayment, "SUCCESS");

        Mockito.verify(veiculos, Mockito.timeout(20000))
            .comprar("ABC1D23", CPF, nextPayment);

        Mockito.verify(veiculos, Mockito.never())
            .comprar("ABC1D23", CPF, id);
    }
}
