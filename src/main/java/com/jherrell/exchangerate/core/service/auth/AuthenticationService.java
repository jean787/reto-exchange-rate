package com.jherrell.exchangerate.core.service.auth;

import com.jherrell.exchangerate.core.model.JwtAuthenticationResponse;
import com.jherrell.exchangerate.core.model.SignInRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface AuthenticationService {

    Mono<JwtAuthenticationResponse> generateJwt(SignInRequest request, ServerWebExchange webExchange);
}
