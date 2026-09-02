package br.nom.rccrv.code.infrastructure.cognito;

import br.nom.rccrv.code.arch.port.service.CreateUserAuthServicePort;
import br.nom.rccrv.code.infrastructure.config.CognitoAdminConfig;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.MessageActionType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import java.util.Objects;

@ApplicationScoped
public class CognitoAdminClient implements CreateUserAuthServicePort {

    private final CognitoAdminConfig config;
    private final CognitoIdentityProviderClient cognito;

    @Inject
    public CognitoAdminClient(CognitoAdminConfig config) {
        this(
            config,
            CognitoIdentityProviderClient.builder()
                .region(Region.of(config.region()))
                .credentialsProvider(credentialsProvider(config))
                .build()
        );
    }

    static AwsCredentialsProvider credentialsProvider(CognitoAdminConfig config) {
        String profile = config.awsProfile().orElse(null);
        if (profile == null || profile.isBlank()) {
            return DefaultCredentialsProvider.create();
        }
        return ProfileCredentialsProvider.create(profile);
    }

    CognitoAdminClient(CognitoAdminConfig config, CognitoIdentityProviderClient cognito) {
        this.config = Objects.requireNonNull(config);
        this.cognito = Objects.requireNonNull(cognito);
    }

    @Override
    public String criarComprador(String username) {
        String userPoolId = config.userPoolId();
        String createdUsername = username;

        try {
            var createdUser = cognito.adminCreateUser(AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .messageAction(MessageActionType.SUPPRESS)
                .build());

            var sub = createdUser.user().attributes().stream()
                .filter(attribute -> "sub".equals(attribute.name()))
                .map(attribute -> attribute.value())
                .findFirst()
                .orElse(username);

            cognito.adminSetUserPassword(AdminSetUserPasswordRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .password(username)
                .permanent(true)
                .build());

            cognito.adminAddUserToGroup(AdminAddUserToGroupRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .groupName(config.compradorGroup())
                .build());

            return sub;
        } catch (UsernameExistsException exception) {
            throw new IllegalArgumentException("Comprador já existe: " + username, exception);
        } catch (RuntimeException exception) {
            try {
                rollbackCriarComprador(createdUsername);
            } catch (RuntimeException rollbackException) {
                exception.addSuppressed(rollbackException);
            }
            throw exception;
        }
    }

    @Override
    public void rollbackCriarComprador(String username) {
        try {
            cognito.adminDeleteUser(AdminDeleteUserRequest.builder()
                .userPoolId(config.userPoolId())
                .username(username)
                .build());
        } catch (software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException ignored) {
            // The desired rollback state has already been reached.
        }
    }

    @PreDestroy
    void close() {
        cognito.close();
    }
}
