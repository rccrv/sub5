package br.nom.rccrv.code.infrastructure.keycloak;

import br.nom.rccrv.code.arch.port.service.CreateUserAuthServicePort;
import br.nom.rccrv.code.infrastructure.config.KeycloakAdminConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@ApplicationScoped
public class KeycloakAdminClient implements CreateUserAuthServicePort {

    private final KeycloakAdminConfig config;
    private final Keycloak keycloak;

    @Inject
    public KeycloakAdminClient(KeycloakAdminConfig config) {
        this.config = config;
        this.keycloak = Keycloak.getInstance(
                config.instance(),
                config.authRealm(),
                config.user(),
                config.password(),
                config.clientId()
        );
    }

    public String criarComprador(String username) {
        UsersResource usersResource = keycloak.realm(config.realm()).users();
        RoleRepresentation compradorRole = keycloak.realm(config.realm())
                .roles()
                .get("comprador")
                .toRepresentation();

        List<UserRepresentation> existingUsers = usersResource.searchByUsername(username, true);
        if (!existingUsers.isEmpty()) {
            throw new IllegalArgumentException("Comprador já existe: " + username);
        }

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEnabled(true);

        CredentialRepresentation passwordCredential = new CredentialRepresentation();
        passwordCredential.setType(CredentialRepresentation.PASSWORD);
        passwordCredential.setValue(username);
        passwordCredential.setTemporary(false);
        user.setCredentials(List.of(passwordCredential));

        try (Response response = usersResource.create(user)) {
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                usersResource.get(userId).roles().realmLevel().add(List.of(compradorRole));
                return userId;
            }

            // NOTE: Para evitar race condition
            if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                throw new IllegalArgumentException("Comprador já existe: " + username);
            }

            throw new WebApplicationException(
                    "Falhou ao criar o usuário. Status: " + response.getStatus(),
                    response.getStatus()
            );
        }
    }
}
