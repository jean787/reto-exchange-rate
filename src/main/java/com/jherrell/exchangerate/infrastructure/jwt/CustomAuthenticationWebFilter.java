package com.jherrell.exchangerate.infrastructure.jwt;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Setter
public class CustomAuthenticationWebFilter implements WebFilter {

    private final ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver;
    private ServerAuthenticationSuccessHandler authenticationSuccessHandler = new WebFilterChainServerAuthenticationSuccessHandler();
    private ServerAuthenticationConverter authenticationConverter = new ServerHttpBasicAuthenticationConverter();
    private ServerAuthenticationFailureHandler authenticationFailureHandler = new ServerAuthenticationEntryPointFailureHandler(new HttpBasicServerAuthenticationEntryPoint());
    private ServerSecurityContextRepository securityContextRepository = NoOpServerSecurityContextRepository.getInstance();

    public CustomAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
        this.authenticationManagerResolver = (request) -> Mono.just(authenticationManager);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final String token = JwtService.getTokenFromRequest(exchange);

        if (token == null) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.defer(() ->
                        this.authenticationConverter.convert(exchange)
                            .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
                            .flatMap(authToken -> authenticate(exchange, chain, authToken))
                            .then(Mono.empty())
                ))
                .flatMap(securityContext -> {
                    log.debug("SecurityContext contains token {}", securityContext.getAuthentication());
                    return chain.filter(exchange);
                })
                .onErrorResume(AuthenticationException.class, (ex) -> {
                    return this.authenticationFailureHandler.onAuthenticationFailure(new WebFilterExchange(exchange, chain), ex);
                });
    }

    private Mono<Void> authenticate(ServerWebExchange exchange, WebFilterChain chain, Authentication token) {
        return this.authenticationManagerResolver.resolve(exchange)
                .flatMap(authenticationManager -> onAuthenticationSuccess(token, exchange, chain))
                .doOnError(AuthenticationException.class, (ex) -> {
                   log.debug("Authentication failed {}", ex.getMessage(), ex);
                });
    }

    private Mono<Void> onAuthenticationSuccess(Authentication authentication, ServerWebExchange exchange,
                                               WebFilterChain chain) {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        securityContextRepository.save(exchange, securityContext)
                .subscribe();

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                .then(this.authenticationSuccessHandler.onAuthenticationSuccess(new WebFilterExchange(exchange, chain), authentication));
    }


}
