package com.lightshell.oauth2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class RegisteredClientServiceImpl {

    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean isExists(String clientId) {
        return null != registeredClientRepository.findByClientId(clientId);
    }

    public void save(String clientId, String password) {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString().replaceAll("-", ""))
            .clientId(clientId).clientSecret(passwordEncoder.encode(password))
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .authorizationGrantType(AuthorizationGrantType.PASSWORD)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN).scope(OidcScopes.OPENID).scope("any")
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
            .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofSeconds(7200))
                .reuseRefreshTokens(true).refreshTokenTimeToLive(Duration.ofSeconds(7800)).build())
            .build();
        try {
            registeredClientRepository.save(registeredClient);
        } catch (Exception ex) {
            log.error(ex.toString());
            throw ex;
        }
    }

}
