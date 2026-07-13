package br.nom.rccrv.code.endpoint.comprador;

import br.nom.rccrv.code.arch.adapter.comprador.CompradorInputAdapter;
import br.nom.rccrv.code.arch.adapter.comprador.CompradorOutputAdapter;
import br.nom.rccrv.code.container.TestcontainerManager;
import br.nom.rccrv.code.domain.dto.CompradorReqDto;
import br.nom.rccrv.code.domain.dto.CompradorRespDto;
import br.nom.rccrv.code.infrastructure.persistence.repository.CompradorRepository;
import br.nom.rccrv.code.utils.TestUtils;
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

    @Inject
    TestUtils testUtils;

    @Test
    void testListarSemToken() {
        RestAssured.given()
            .when()
            .get("/listar")
            .then()
            .statusCode(401);
    }

    @Test
    void testListarCompradoresSemCompradores() {
        String accessToken = testUtils.getAccessToken("funcionario");

        var resp = RestAssured.given()
            .auth()
            .oauth2(accessToken)
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
            CompradorOutputAdapter.paraJpa(
                CompradorInputAdapter.deReqDto(comprador)
            )
        );

        String accessToken = testUtils.getAccessToken("funcionario");

        var resp = RestAssured.given()
            .auth()
            .oauth2(accessToken)
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
}
