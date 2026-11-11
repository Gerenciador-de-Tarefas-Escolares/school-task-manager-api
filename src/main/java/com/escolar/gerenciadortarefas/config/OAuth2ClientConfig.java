package com.escolar.gerenciadortarefas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

@Configuration
public class OAuth2ClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {

        String clientId = System.getProperty("GOOGLE_CLIENT_ID");
        String clientSecret = System.getProperty("GOOGLE_CLIENT_SECRET");

        if (clientId == null) clientId = System.getenv("GOOGLE_CLIENT_ID");
        if (clientSecret == null) clientSecret = System.getenv("GOOGLE_CLIENT_SECRET");

        ClientRegistration googleRegistration = ClientRegistration.withRegistrationId("google")
                .clientId(clientId != null ? clientId : "id-nao-encontrado")
                .clientSecret(clientSecret != null ? clientSecret : "secret-nao-encontrado")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email")
                .authorizationUri("https://google.com")
                .tokenUri("https://googleapis.com")
                .userInfoUri("https://googleapis.com")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri("https://googleapis.com")
                .clientName("Google")
                .build();

        return new InMemoryClientRegistrationRepository(googleRegistration);
    }
}
