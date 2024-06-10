package com.jherrell.exchangerate.api;

import com.jherrell.exchangerate.core.model.JwtAuthenticationResponse;
import com.jherrell.exchangerate.core.model.SignInRequest;
import com.jherrell.exchangerate.core.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("auth/v1")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthenticationController {

    private AuthenticationService authService;

    @Operation(
            operationId = "login",
            summary = "Login",
            description = "Login ",
            tags = { "Login" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Generar token", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthenticationResponse.class))
                    })
            }
    )
    @PostMapping(value = "/jwt")
    public Mono<ResponseEntity<JwtAuthenticationResponse>>login(@Valid @RequestBody SignInRequest request, ServerWebExchange webExchange) {
        return authService.generateJwt(request, webExchange)
                .map(ResponseEntity::ok);
    }
}
