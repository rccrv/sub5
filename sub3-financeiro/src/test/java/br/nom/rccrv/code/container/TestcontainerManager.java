package br.nom.rccrv.code.container;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

import java.util.HashMap;
import java.util.Map;

public class TestcontainerManager implements QuarkusTestResourceLifecycleManager {
    private PostgreSQLContainer postgres;
    private KafkaContainer kafka;
    private KafkaCompanion companion;

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
                + ":" + postgres.getMappedPort(5432) + "/financeiro");
            config.put("quarkus.datasource.username", "sub3");
            config.put("quarkus.datasource.password", "sub3");
            config.put("quarkus.hibernate-orm.schema-management.strategy", "none");
            kafka = new KafkaContainer("apache/kafka-native:4.1.2");
            kafka.start();
            companion = new KafkaCompanion(kafka.getBootstrapServers());
            companion.topics()
                .createAndWait("financeiro", 1);
            companion.topics()
                .createAndWait("financeiro-ack", 1);
            config.put("kafka.bootstrap.servers", kafka.getBootstrapServers());
            config.put("mp.messaging.incoming.orquestrador-in.auto.offset.reset", "earliest");
            return config;
        } catch (RuntimeException exception) {
            stop();
            throw exception;
        }
    }

    @Override
    public void stop() {
        if (companion != null) {
            companion.close();
        }
        if (kafka != null) {
            kafka.stop();
        }
        if (postgres != null) {
            postgres.stop();
        }
    }

    @Override
    public void inject(TestInjector injector) {
        injector.injectIntoFields(companion,
            new TestInjector.AnnotatedAndMatchesType(InjectKafkaCompanion.class, KafkaCompanion.class));
    }
}
