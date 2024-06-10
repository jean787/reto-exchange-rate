package com.jherrell.exchangerate.infrastructure.config;

import static com.jherrell.exchangerate.infrastructure.jwt.JwtService.getTokenFromRequest;
import static com.jherrell.exchangerate.infrastructure.jwt.JwtService.getUsernameFromToken;

import com.jherrell.exchangerate.infrastructure.jwt.CustomAuthenticationWebFilter;
import com.jherrell.exchangerate.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final ApplicationProperties properties;

    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        WebSessionServerSecurityContextRepository securityContextRepository =
                new WebSessionServerSecurityContextRepository();

        securityContextRepository.setSpringSecurityContextAttrName("securityContext");

        return securityContextRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {

        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailService());

        authenticationManager.setPasswordEncoder(passwordEncoder());

        return authenticationManager;
    }

    @Bean
    public ReactiveUserDetailsService userDetailService() {
        return username -> Mono.just(userRepository.findByUsername(username));
    }

    @Bean
    public CustomAuthenticationWebFilter authenticationWebFilter() {
        CustomAuthenticationWebFilter filter = new CustomAuthenticationWebFilter(authenticationManager());
        filter.setAuthenticationConverter(authenticationConverter());
        filter.setSecurityContextRepository(securityContextRepository());

        return filter;
    }

    @Bean
    public ServerAuthenticationConverter authenticationConverter() {
        return exchange -> {

            final String token = getTokenFromRequest(exchange);
            String username = getUsernameFromToken(token, properties.getSecret());

            return userDetailService().findByUsername(username)
                    .map(userDetails -> {
                        return new UsernamePasswordAuthenticationToken(
                                userDetails.getUsername(),
                                null,
                                userDetails.getAuthorities());
                    });
        };
    }

    @Bean
    public SecurityWebFilterChain securityWebFiltersOrder(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("auth/v1/**").permitAll()
                        .anyExchange().authenticated())
                .securityContextRepository(securityContextRepository())
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .build();
    }
}
