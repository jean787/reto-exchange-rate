package com.jherrell.exchangerate.api;

import com.jherrell.exchangerate.core.model.ExchangeRateInquiryResponse;
import com.jherrell.exchangerate.core.model.ExchangeRateRequest;
import com.jherrell.exchangerate.core.model.ExchangeRateResponse;
import com.jherrell.exchangerate.core.service.exchangerate.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("exchange-rate/v1")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ExchangeRateController {

    private ExchangeRateService exchangeRateService;

    @Operation(
            operationId = "executeExchangeRate",
            summary = "Realiza la ejecución para el tipo de cambio de moneda extranjera",
            description = "Ejecuta tipo de cambio",
            tags = { "Exchange Rate" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuarios", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExchangeRateResponse.class))
                    })
            }
    )
    @PostMapping("/initiate")
    public Mono<ResponseEntity<ExchangeRateResponse>> exchangeRateProcess(@RequestHeader("Authorization") String authorizationHeader,
                                                                          @Valid @RequestBody ExchangeRateRequest request) {
        System.out.println("Ingreso");
        return exchangeRateService.executeExchangeRate(request, authorizationHeader)
                .map(ResponseEntity::ok);
    }

    @Operation(
            operationId = "inquiryExchangeRate",
            summary = "Consulta los registros guardados del tipo de cambio",
            description = "Registros de tipo de cambio",
            tags = { "Exchange Rate Inquiry" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuarios", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExchangeRateInquiryResponse.class))
                    })
            }
    )
    @GetMapping("/retrieve")
    public Mono<ResponseEntity<Flux<ExchangeRateInquiryResponse>>> exchangeRateInquiry(){
        return Mono.just(exchangeRateService.inquiryExchangeRate())
                .map(ResponseEntity::ok);
    }

}
