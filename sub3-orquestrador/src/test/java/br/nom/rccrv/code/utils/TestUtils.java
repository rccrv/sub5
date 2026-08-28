package br.nom.rccrv.code.utils;

import io.restassured.RestAssured;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.representations.AccessTokenResponse;

@ApplicationScoped
public class TestUtils {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String realmUrl;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String clientSecret;

    public String getAccessToken(String username) {
        return RestAssured.given()
            .param("username", username)
            .param("password", username)
            .param("grant_type", "password")
            .param("client_id", clientId)
            .param("client_secret", clientSecret)
            .when()
            .post(realmUrl + "/protocol/openid-connect/token")
            .then()
            .statusCode(200)
            .extract()
            .as(AccessTokenResponse.class)
            .getToken();
    }
}
