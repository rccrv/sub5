package br.nom.rccrv.code.endpoint.comprador;

import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.CompradorReqDto;
import br.nom.rccrv.code.domain.dto.CompradorRespDto;
import br.nom.rccrv.code.infrastructure.persistence.repository.CompradorRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(TestcontainerManager.class)
public class CadastrarCompradorEndpointTest {

    @Inject
    CompradorRepository compradorRepository;

    @Test
    void testCadastroInvalido() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CompradorReqDto(
                "123.456.789-10",
                "Comprador",
                "Falha",
                "falha@falha.com",
                "(11) 91234-5678"
            )
        );
    }

    @Test
    void testCadastroValido() {
        var req = new CompradorReqDto(
            "822.765.420-71",
            "Comprador 2",
            "Sucesso",
            "comprador2@comprador2.com",
            "(11) 91234-5678"
        );

        var resp = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(req)
            .when()
            .post("/cadastrar")
            .then()
            .statusCode(201)
            .extract()
            .as(CompradorRespDto.class);

        var entityJpa = compradorRepository.findById(resp.id()).orElseThrow();
        Assertions.assertNotNull(entityJpa);
        Assertions.assertEquals(req.cpf(), entityJpa.getCpf());

        compradorRepository.delete(entityJpa);
    }
}
