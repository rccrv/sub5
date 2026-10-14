package br.nom.rccrv.code.container;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

import java.util.HashMap;
import java.util.Map;

public class TestcontainerManager implements QuarkusTestResourceLifecycleManager {
    private PostgreSQLContainer postgres;
    private KeycloakContainer keycloak;

    @Override
    public Map<String, String> start() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("quarkus.devservices.enabled", "false");
            // The cluster dump contains psql commands and must run through the image entrypoint.
            postgres = new PostgreSQLContainer("postgres:18.4")
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("postgres")
                .withCopyFileToContainer(MountableFile.forClasspathResource("ddl.sql"),
                    "/docker-entrypoint-initdb.d/01-ddl.sql");
            postgres.start();
            config.put("quarkus.datasource.jdbc.url", "jdbc:postgresql://" + postgres.getHost()
                + ":" + postgres.getMappedPort(5432) + "/compradores");
            config.put("quarkus.datasource.username", "sub3");
            config.put("quarkus.datasource.password", "sub3");
            config.put("quarkus.hibernate-orm.schema-management.strategy", "none");
            keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.6")
                .withAdminUsername("sub3")
                .withAdminPassword("sub3")
                .withRealmImportFile("sub3-realm.json");
            keycloak.start();
            config.put("quarkus.oidc.auth-server-url", keycloak.getAuthServerUrl() + "/realms/sub3");
            config.put("quarkus.oidc.client-id", "compradores");
            config.put("quarkus.oidc.credentials.secret", "WiGPbgwfBBwoouEdqnVdtJVsaUG3deb2");
            config.put("quarkus.oidc.roles.role-claim-path", "realm_access/roles");
            config.put("quarkus.oidc.token.principal-claim", "preferred_username");
            config.put("keycloak.admin.instance", keycloak.getAuthServerUrl());
            config.put("keycloak.admin.user", "sub3");
            config.put("keycloak.admin.password", "sub3");
            return config;
        } catch (RuntimeException exception) {
            stop();
            throw exception;
        }
    }

    @Override
    public void stop() {
        if (keycloak != null) {
            keycloak.stop();
        }
        if (postgres != null) {
            postgres.stop();
        }
    }
}
