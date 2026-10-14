package br.nom.rccrv.code.container;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

import java.util.HashMap;
import java.util.Map;

public class TestcontainerManager implements QuarkusTestResourceLifecycleManager {
    private PostgreSQLContainer postgres;

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
                + ":" + postgres.getMappedPort(5432) + "/principal");
            config.put("quarkus.datasource.username", "sub3");
            config.put("quarkus.datasource.password", "sub3");
            config.put("quarkus.hibernate-orm.schema-management.strategy", "none");
            return config;
        } catch (RuntimeException exception) {
            stop();
            throw exception;
        }
    }

    @Override
    public void stop() {
        if (postgres != null) {
            postgres.stop();
        }
    }
}
