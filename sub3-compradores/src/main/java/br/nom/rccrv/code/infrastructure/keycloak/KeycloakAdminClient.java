package br.nom.rccrv.code.infrastructure.keycloak;

import br.nom.rccrv.code.arch.port.service.CreateUserAuthServicePort;
import br.nom.rccrv.code.infrastructure.config.KeycloakAdminConfig;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@Alternative
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

    @Override
    public String criarComprador(String username) {
        var realm = keycloak.realm(config.realm());
        var users = realm.users();
        var compradorRole = realm.roles().get("comprador").toRepresentation();

        if (!users.searchByUsername(username, true).isEmpty()) {
            throw new IllegalArgumentException("Comprador já existe: " + username);
        }

        var credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(username);
        credential.setTemporary(false);

        var user = new UserRepresentation();
        user.setUsername(username);
        user.setEnabled(true);
        user.setCredentials(List.of(credential));

        String userId;
        try (Response response = users.create(user)) {
            int status = response.getStatus();

            if (status == Response.Status.CONFLICT.getStatusCode()) {
                throw new IllegalArgumentException("Comprador já existe: " + username);
            }

            if (status != Response.Status.CREATED.getStatusCode()) {
                throw new WebApplicationException(
                    "Falhou ao criar o usuário. Status: " + status,
                    status
                );
            }

            userId = CreatedResponseUtil.getCreatedId(response);
        }

        try {
            users.get(userId).roles().realmLevel().add(List.of(compradorRole));
        } catch (RuntimeException exception) {
            try {
                deleteById(users, userId);
            } catch (RuntimeException rollbackException) {
                exception.addSuppressed(rollbackException);
            }
            throw exception;
        }

        return userId;
    }

    @Override
    public void rollbackCriarComprador(String username) {
        var users = keycloak.realm(config.realm()).users();

        for (var user : users.searchByUsername(username, true)) {
            deleteById(users, user.getId());
        }
    }

    private void deleteById(UsersResource users, String userId) {
        try (Response response = users.delete(userId)) {
            int status = response.getStatus();
            boolean deleted = response.getStatusInfo().getFamily()
                == Response.Status.Family.SUCCESSFUL;
            boolean absent = status == Response.Status.NOT_FOUND.getStatusCode();

            if (!deleted && !absent) {
                throw new WebApplicationException(
                    "Falhou ao desfazer a criação do usuário. Status: " + status,
                    status
                );
            }
        }
    }

    @PreDestroy
    void close() {
        keycloak.close();
    }
}
