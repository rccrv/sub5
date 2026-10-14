package br.nom.rccrv.code.api.endpoint;

import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.pagamento.PagamentoRespDto;
import br.nom.rccrv.code.domain.dto.pagamento.PagarPagamentoReqDto;
import br.nom.rccrv.code.domain.dto.pagamento.ReservaPagamentoReqDto;
import br.nom.rccrv.code.infrastructure.persistence.repository.PagamentoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.restassured.RestAssured;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@QuarkusTest
@QuarkusTestResource(TestcontainerManager.class)
class PagamentoResourceTest {

    private static final String CPF = "73985377359";

    @Inject
    PagamentoRepository repository;

    @Inject
    ObjectMapper mapper;

    @Inject
    EntityManager entityManager;

    @InjectKafkaCompanion
    KafkaCompanion companion;

    @AfterEach
    void limparBanco() {
        try (var entities = repository.findAll()) {
            repository.deleteAll(entities.toList());
        }
    }

    private PagamentoRespDto reservarPagamento() {
        return RestAssured.given()
            .contentType("application/json")
            .body(new ReservaPagamentoReqDto(
                CPF,
                "ABC1D23",
                "Rua Teste 1",
                "01001000",
                new BigDecimal("30000")
            ))
            .when()
            .post("/internal/pagamentos/reservas")
            .then()
            .statusCode(201)
            .body("status", Matchers.is("PENDING"))
            .extract()
            .as(PagamentoRespDto.class);
    }

    @Test
    void testReservarPagamentoEConsultarSomenteComoTitular() {
        var payment = reservarPagamento();

        Assertions.assertTrue(repository.findByCorrelationId(payment.id())
            .isPresent());

        RestAssured.given()
            .header("X-CPF", CPF)
            .when()
            .get("/internal/pagamentos/" + payment.id())
            .then()
            .statusCode(200)
            .body("pixCode", Matchers.is(payment.pixCode()));

        RestAssured.given()
            .header("X-CPF", "82276542071")
            .when()
            .get("/internal/pagamentos/" + payment.id())
            .then()
            .statusCode(404);

        RestAssured.given()
            .header("X-CPF", CPF)
            .when()
            .get("/internal/pagamentos/" + UUID.randomUUID())
            .then()
            .statusCode(404);
    }

    @Test
    void testLiquidarPagamentoEPublicarAckDeSucesso() throws Exception {
        var payment = reservarPagamento();
        try (var events = companion.consumeStrings()
            .withGroupId(UUID.randomUUID()
                .toString())
            .withProp("auto.offset.reset", "earliest")
            .fromTopics(Set.of("financeiro-ack"), records -> records
                .select()
                .where(record -> record.value()
                    .contains(payment.id()
                        .toString()))
                .select()
                .first(1))
        ) {
            RestAssured.given()
                .header("X-CPF", CPF)
                .contentType("application/json")
                .body(new PagarPagamentoReqDto(payment.pixCode()))
                .when()
                .post("/internal/pagamentos/" + payment.id() + "/pagar")
                .then()
                .statusCode(202)
                .body("status", Matchers.is("PAID"))
                .body("settledAmount", Matchers.is(30000.0f));
            events.awaitCompletion(Duration.ofSeconds(20));
            var ack = mapper.readTree(events.getFirstRecord()
                .value());

            Assertions.assertEquals(payment.id()
                .toString(), ack.get("transactionId")
                    .asText());

            Assertions.assertEquals("SUCCESS", ack.get("status")
                .asText());

            Assertions.assertEquals(CPF, ack.get("cpf")
                .asText());

            Assertions.assertEquals("ABC1D23", ack.get("placa")
                .asText());
        }
        var pagamentoJpa = repository.findByCorrelationId(payment.id())
            .orElseThrow();

        Assertions.assertEquals("PAID", pagamentoJpa.status.name());
    }

    @Test
    void testPixIncorretoPublicaAckDeFalhaEMantemPagamentoPendente() throws Exception {
        var payment = reservarPagamento();
        try (var events = companion.consumeStrings()
            .withGroupId(UUID.randomUUID()
                .toString())
            .withProp("auto.offset.reset", "earliest")
            .fromTopics(Set.of("financeiro-ack"), records -> records
                .select()
                .where(record -> record.value()
                    .contains(payment.id()
                        .toString()))
                .select()
                .first(1))) {
            RestAssured.given()
                .header("X-CPF", CPF)
                .contentType("application/json")
                .body(new PagarPagamentoReqDto("wrong"))
                .when()
                .post("/internal/pagamentos/" + payment.id() + "/pagar")
                .then()
                .statusCode(404);
            events.awaitCompletion(Duration.ofSeconds(20));

            Assertions.assertEquals("FAILED", mapper.readTree(events.getFirstRecord()
                .value())
                .get("status")
                .asText());
        }
        var pagamentoJpa = repository.findByCorrelationId(payment.id())
            .orElseThrow();

        Assertions.assertEquals("PENDING", pagamentoJpa.status.name());
    }

    @Test
    void testConsumirCompensacaoKafkaEPersistirCancelamento() throws Exception {
        var payment = reservarPagamento();
        String command = mapper.writeValueAsString(Map.of("pagamentoId", payment.id(),
            "transactionId", UUID.randomUUID(), "stepType", "ROLLBACK", "type", "CancelaPagamentoDto"));
        companion.produceStrings()
            .fromRecords(new ProducerRecord<>("financeiro", command))
            .awaitCompletion(Duration.ofSeconds(20));
        Awaitility.await()
            .atMost(Duration.ofSeconds(20))
            .untilAsserted(() ->
                RestAssured.given()
                    .header("X-CPF", CPF)
                    .when()
                    .get("/internal/pagamentos/" + payment.id())
                    .then()
                    .statusCode(200)
                    .body("status", Matchers.is("CANCELLED")));
    }

    @Test
    void testPagamentoExpiradoNaoPodeSerPago() {
        var payment = reservarPagamento();
        QuarkusTransaction.requiringNew()
            .run(() ->
                entityManager.createQuery("update Pagamento p set p.expiresAt = :expiry where p.correlationId = :id")
                    .setParameter("expiry", LocalDateTime.now()
                        .minusMinutes(1))
                    .setParameter("id", payment.id())
                    .executeUpdate());

        RestAssured.given()
            .header("X-CPF", CPF)
            .contentType("application/json")
            .body(new PagarPagamentoReqDto(payment.pixCode()))
            .when()
            .post("/internal/pagamentos/" + payment.id() + "/pagar")
            .then()
            .statusCode(404);
        var pagamentoJpa = repository.findByCorrelationId(payment.id())
            .orElseThrow();

        Assertions.assertEquals("EXPIRED", pagamentoJpa.status.name());
    }

    @Test
    void testValidarRequisicoesDeReservaEPagamento() {
        RestAssured.given()
            .contentType("application/json")
            .body(new ReservaPagamentoReqDto(null, null, null, null, null))
            .when()
            .post("/internal/pagamentos/reservas")
            .then()
            .statusCode(400);
        var payment = reservarPagamento();

        RestAssured.given()
            .header("X-CPF", CPF)
            .contentType("application/json")
            .body(new PagarPagamentoReqDto(""))
            .when()
            .post("/internal/pagamentos/" + payment.id() + "/pagar")
            .then()
            .statusCode(400);
    }
}
