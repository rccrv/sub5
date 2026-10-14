package br.nom.rccrv.code.container;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import org.testcontainers.kafka.KafkaContainer;

import java.util.HashMap;
import java.util.Map;

public class TestcontainerManager implements QuarkusTestResourceLifecycleManager {
    private KeycloakContainer keycloak;
    private KafkaContainer kafka;
    private KafkaCompanion companion;

    @Override
    public Map<String, String> start() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("quarkus.devservices.enabled", "false");
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
            kafka = new KafkaContainer("apache/kafka-native:4.1.2");
            kafka.start();
            companion = new KafkaCompanion(kafka.getBootstrapServers());
            companion.topics()
                .createAndWait("financeiro", 1);
            companion.topics()
                .createAndWait("financeiro-ack", 1);
            config.put("kafka.bootstrap.servers", kafka.getBootstrapServers());
            config.put("mp.messaging.incoming.financeiro-in.auto.offset.reset", "earliest");
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
        if (keycloak != null) {
            keycloak.stop();
        }
    }

    @Override
    public void inject(TestInjector injector) {
        injector.injectIntoFields(companion,
            new TestInjector.AnnotatedAndMatchesType(InjectKafkaCompanion.class, KafkaCompanion.class));
    }
}
