package br.nom.rccrv.code.container;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.HashMap;
import java.util.Map;

public class TestcontainerManager implements QuarkusTestResourceLifecycleManager {

    private KeycloakContainer keycloakContainer;

    @Override
    public Map<String, String> start() {
        keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:26.6")
            .withAdminUsername("admin")
            .withAdminPassword("admin")
            .withRealmImportFile("/sub3-realm.json");
        keycloakContainer.start();

        var authServerUrl = keycloakContainer.getAuthServerUrl() + "/realms/sub3";
        var config = new HashMap<String, String>();
        config.put("quarkus.oidc.enabled", "true");
        config.put("quarkus.oidc.auth-server-url", authServerUrl);
        config.put("quarkus.oidc.client-id", "compradores");
        config.put("quarkus.oidc.credentials.secret", "WiGPbgwfBBwoouEdqnVdtJVsaUG3deb2");
        config.put("quarkus.oidc-client.auth-server-url", authServerUrl);
        config.put("quarkus.oidc-client.client-id", "compradores");
        config.put("quarkus.oidc-client.credentials.secret", "WiGPbgwfBBwoouEdqnVdtJVsaUG3deb2");
        config.put("quarkus.oidc.authentication.verify-access-token", "true");
        return config;
    }

    @Override
    public void stop() {
        if (keycloakContainer != null) {
            keycloakContainer.stop();
        }
    }
}
