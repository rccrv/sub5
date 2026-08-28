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
public class ListarCompradoresParaAutorizarEndpointTest {

    @Inject
    CompradorRepository compradorRepository;

    @Test
    void testListarCompradoresSemCompradores() {
        var resp = RestAssured.given()
            .when()
            .get("/listar")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .getList(".", CompradorRespDto.class);

        Assertions.assertNotNull(resp);
        Assertions.assertTrue(resp.isEmpty());
    }

    @Test
    void testListarCompradoresComCompradores() {
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
            .when()
            .get("/listar")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .getList(".", CompradorRespDto.class);

        Assertions.assertNotNull(resp);
        Assertions.assertFalse(resp.isEmpty());
        Assertions.assertEquals(compradorJpa.getCpf(), resp.getFirst().cpf());

        compradorRepository.delete(compradorJpa);
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
