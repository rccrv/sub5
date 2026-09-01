package br.nom.rccrv.code.infrastructure.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "cognito")
public interface CognitoAdminConfig {

    @WithDefault("sa-east-1")
    String region();

    @WithDefault("")
    String userPoolId();

    @WithDefault("")
    String awsProfile();

    @WithDefault("comprador")
    String compradorGroup();
}
