package com.jherrell.exchangerate.core.service.auth;

import com.jherrell.exchangerate.core.Exception.ApiException;
import com.jherrell.exchangerate.core.model.JwtAuthenticationResponse;
import com.jherrell.exchangerate.core.model.SignInRequest;
import com.jherrell.exchangerate.infrastructure.config.ApplicationProperties;
import com.jherrell.exchangerate.infrastructure.jwt.JwtService;
import com.jherrell.exchangerate.infrastructure.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService{

    private UserRepository userRepository;
    private ReactiveAuthenticationManager authenticationManager;
    private ServerSecurityContextRepository securityContextRepository;
    private ApplicationProperties properties;

    @Override
    public Mono<JwtAuthenticationResponse> generateJwt(SignInRequest request, ServerWebExchange exchange) {

        return authenticate(request)
                .flatMap(authentication -> saveSecurityContext(authentication, exchange))
                .then(generateToken(request.getUsername()))
                .onErrorResume(AuthenticationException.class, (ex) -> Mono.error(new ApiException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    private Mono<Authentication> authenticate(SignInRequest request) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    }

    private Mono<Void> saveSecurityContext(Authentication authentication, ServerWebExchange exchange) {
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        return securityContextRepository
                .save(exchange, securityContext)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
    }

    private Mono<JwtAuthenticationResponse> generateToken(String username) {
        return Mono.just(userRepository.findByUsername(username))
                .map(userEntity -> JwtService.getToken(userEntity, properties.getSecret()))
                .map(token -> JwtAuthenticationResponse.builder().token(token).build());
    }
}
