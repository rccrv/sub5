package br.nom.rccrv.code.api.endpoint.comprador;

import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.CompradorReqDto;
import br.nom.rccrv.code.domain.dto.CompradorRespDto;
import br.nom.rccrv.code.infrastructure.persistence.entity.Comprador;
import br.nom.rccrv.code.infrastructure.persistence.repository.CompradorRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(TestcontainerManager.class)
public class AutorizarCompradorEndpointTest {

    @Inject
    CompradorRepository compradorRepository;

    @Test
    void testAutorizarSemToken() {
        var comprador = new CompradorReqDto(
            "822.765.420-71",
            "Comprador 2",
            "Sucesso",
            "comprador2@comprador2.com",
            "(11) 91234-5678"
        );

        var compradorJpa = compradorRepository.insert(
            compradorJpa(comprador)
        );

        var resp = RestAssured.given()
            .pathParam("cpf", comprador.cpf())
            .when()
            .put("/autorizar/{cpf}")
            .then()
            .statusCode(200)
            .extract()
            .as(CompradorRespDto.class);

        Assertions.assertNotNull(resp);
        Assertions.assertEquals(compradorJpa.getCpf(), resp.cpf());
        Assertions.assertTrue(resp.autorizado());

        compradorRepository.delete(compradorJpa);
    }

    @Test
    void testAutorizarCompradorInexistente() {
        RestAssured.given()
            .pathParam("cpf", "822.765.420-71")
            .when()
            .put("/autorizar/{cpf}")
            .then()
            .statusCode(404);
    }

    private static Comprador compradorJpa(CompradorReqDto dto) {
        var entity = new Comprador();
        entity.setCpf(dto.cpf());
        entity.setPrimeiroNome(dto.primeiroNome());
        entity.setUltimoNome(dto.ultimoNome());
        entity.setEmail(dto.email());
        entity.setTelefone(dto.telefone());
        entity.setAutorizado(false);
        return entity;
    }
}
