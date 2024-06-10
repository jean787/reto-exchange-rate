package com.jherrell.exchangerate.core.service.auth;

import com.jherrell.exchangerate.core.Exception.ApiException;
import com.jherrell.exchangerate.core.model.JwtAuthenticationResponse;
import com.jherrell.exchangerate.core.model.SignInRequest;
import com.jherrell.exchangerate.infrastructure.config.ApplicationProperties;
import com.jherrell.exchangerate.infrastructure.entity.UserEntity;
import com.jherrell.exchangerate.infrastructure.jwt.JwtService;
import com.jherrell.exchangerate.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl service;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReactiveAuthenticationManager authenticationManager;
    @Mock
    private ServerSecurityContextRepository securityContextRepository;
    @Mock
    private ApplicationProperties properties;

    @Test
    void generateJwt_success() {

        SignInRequest request = new SignInRequest("username", "password");
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        Authentication authentication = mock(Authentication.class);
        UserEntity userEntity = mock(UserEntity.class);
        String token = "token";

        when(authenticationManager.authenticate(any())).thenReturn(Mono.just(authentication));
        when(securityContextRepository.save(any(), any())).thenReturn(Mono.empty());
        when(userRepository.findByUsername(any())).thenReturn(userEntity);
        when(properties.getSecret()).thenReturn("586S3272357838782F413F7428472B4U4550655568666B597033733676397524");
        when(JwtService.getToken(userEntity, properties.getSecret())).thenReturn(token);


        Mono<JwtAuthenticationResponse> result = service.generateJwt(request, exchange);


        StepVerifier.create(result)
                .expectNextMatches(response -> response.getToken() != null)
                .verifyComplete();
    }

    @Test
    void generateJwt_authenticationError() {
        SignInRequest request = new SignInRequest("username", "password");
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        AuthenticationException exception = mock(AuthenticationException.class);
        UserEntity userEntity = mock(UserEntity.class);

        when(authenticationManager.authenticate(any())).thenReturn(Mono.error(exception));
        when(userRepository.findByUsername(any())).thenReturn(userEntity);

        Mono<JwtAuthenticationResponse> result = service.generateJwt(request, exchange);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ApiException)
                .verify();
    }

    @Test
    void generateJwt_securityContextError() {
        SignInRequest request = new SignInRequest("username", "password");
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        Authentication authentication = mock(Authentication.class);
        RuntimeException exception = new RuntimeException("Security context error");
        UserEntity userEntity = mock(UserEntity.class);

        when(authenticationManager.authenticate(any())).thenReturn(Mono.just(authentication));
        when(userRepository.findByUsername(any())).thenReturn(userEntity);
        when(securityContextRepository.save(any(), any())).thenReturn(Mono.error(exception));


        Mono<JwtAuthenticationResponse> result = service.generateJwt(request, exchange);


        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException)
                .verify();
    }
}
