package br.nom.rccrv.code.container;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.Network;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.HashMap;
import java.util.Map;

public class TestcontainerManager implements QuarkusTestResourceLifecycleManager {

    PostgreSQLContainer postgresContainer;
    KeycloakContainer keycloakContainer;

    @Override
    public Map<String, String> start() {
        Network network = Network.newNetwork();
        String networkAlias = "containers";

        postgresContainer = new PostgreSQLContainer("postgres:18.4")
            .withNetwork(network)
            .withNetworkAliases(networkAlias)
            .withDatabaseName("compradores")
            .withUsername("sub3")
            .withPassword("sub3");

        keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:26.6")
            .withAdminUsername("sub3")
            .withAdminPassword("sub3")
            .withNetwork(network)
            .withNetworkAliases(networkAlias)
            .withExposedPorts(9000, 8080);

        postgresContainer.withInitScript("ddl.sql");
        keycloakContainer.withRealmImportFile("/sub3-realm.json");
        keycloakContainer.start();
        postgresContainer.start();

        Map<String, String> config = new HashMap<>();
        config.put("quarkus.datasource.db-kind", "postgresql");
        config.put("quarkus.datasource.username", "sub3");
        config.put("quarkus.datasource.password", "sub3");
        config.put("quarkus.datasource.jdbc.url", postgresContainer.getJdbcUrl());
        config.put("quarkus.oidc.enabled", "true");
        // NOTE: Necessário pro @RolesAllowed dos endpoints
        config.put("quarkus.oidc.auth-server-url", keycloakContainer.getAuthServerUrl() + "/realms/sub3");
        config.put("quarkus.oidc.client-id", "principal");
        config.put("quarkus.oidc.credentials.secret", "jfkMfo25oWyJHHB9MAc3pwJ7k1ZKzK1L");
        // NOTE: Necessário pro TestUtils
        config.put("quarkus.oidc-client.auth-server-url", keycloakContainer.getAuthServerUrl() + "/realms/sub3");
        config.put("quarkus.oidc-client.client-id", "principal");
        config.put("quarkus.oidc-client.credentials.secret", "jfkMfo25oWyJHHB9MAc3pwJ7k1ZKzK1L");
        config.put("quarkus.oidc.authentication.verify-access-token", "true");

        return config;
    }

    @Override
    public void stop() {
        if (postgresContainer != null) {
            postgresContainer.stop();
        }
        if (keycloakContainer != null) {
            keycloakContainer.stop();
        }
    }
}
