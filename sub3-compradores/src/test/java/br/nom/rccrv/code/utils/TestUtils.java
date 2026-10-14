package br.nom.rccrv.code.utils;

import io.restassured.RestAssured;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class TestUtils {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String realmURL;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String clientSecret;

    public String getAccessToken(String username) {
        return RestAssured.given()
            .formParam("username", username)
            .formParam("password", username)
            .formParam("grant_type", "password")
            .formParam("client_id", clientId)
            .formParam("client_secret", clientSecret)
            .when()
            .post(realmURL + "/protocol/openid-connect/token")
            .then()
            .statusCode(200)
            .extract()
            .path("access_token");
    }
}
