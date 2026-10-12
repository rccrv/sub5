package br.nom.rccrv.code.infrastructure.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "keycloak.admin")
public interface KeycloakAdminConfig {

    @WithDefault("http://localhost:8080")
    String instance();

    @WithDefault("admin")
    String user();

    @WithDefault("admin")
    String password();

    @WithDefault("master")
    String authRealm();

    @WithDefault("sub3")
    String realm();

    @WithDefault("admin-cli")
    String clientId();
}
