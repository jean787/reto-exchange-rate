package com.jherrell.exchangerate.infrastructure.advice;

import com.jherrell.exchangerate.core.Exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static com.jherrell.exchangerate.core.common.Constants.MESSAGE_FIELDS_EMPTY;


@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> apiExceptionHandlerGlobal(Exception ex){
        ApiException apiException = new ApiException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiException, apiException.getErrorCategory());
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiException> apiExceptionHandler(
            ApiException apiException){
        System.out.println("Entro handler");
        return new ResponseEntity<>(apiException, apiException.getErrorCategory());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        String message = ex.getBindingResult().getAllErrors()
                .stream().findFirst()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return "["+fieldName+"]" + " - " + errorMessage;
                })
                .orElse(MESSAGE_FIELDS_EMPTY);
        
        ApiException apiException = new ApiException(message, null);
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }
}
