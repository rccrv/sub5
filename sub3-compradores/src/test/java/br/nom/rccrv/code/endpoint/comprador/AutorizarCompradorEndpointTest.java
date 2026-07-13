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
public class AutorizarCompradorEndpointTest {

    @Inject
    CompradorRepository compradorRepository;

    @Inject
    TestUtils testUtils;

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
            CompradorOutputAdapter.paraJpa(
                CompradorInputAdapter.deReqDto(comprador)
            )
        );

        RestAssured.given()
            .pathParam("cpf", comprador.cpf())
            .when()
            .put("/autorizar/{cpf}")
            .then()
            .statusCode(401);

        compradorRepository.delete(compradorJpa);
    }

    @Test
    void testAutorizarComToken() {
        String accessToken = testUtils.getAccessToken("funcionario");

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

        var resp = RestAssured.given()
            .auth()
            .oauth2(accessToken)
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
}
