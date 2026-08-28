package br.nom.rccrv.code.container;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.Network;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.HashMap;
import java.util.Map;

public class TestcontainerManager implements QuarkusTestResourceLifecycleManager {

    PostgreSQLContainer postgresContainer;

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

        postgresContainer.withInitScript("ddl.sql");
        postgresContainer.start();

        Map<String, String> config = new HashMap<>();
        config.put("quarkus.datasource.db-kind", "postgresql");
        config.put("quarkus.datasource.username", "sub3");
        config.put("quarkus.datasource.password", "sub3");
        config.put("quarkus.datasource.jdbc.url", postgresContainer.getJdbcUrl());

        return config;
    }

    @Override
    public void stop() {
        if (postgresContainer != null) {
            postgresContainer.stop();
        }
    }
}
