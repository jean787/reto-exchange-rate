package com.jherrell.exchangerate.core.Exception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonAutoDetect(
        creatorVisibility = Visibility.NONE,
        fieldVisibility = Visibility.NONE,
        getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE
)
@JsonInclude(Include.NON_NULL)
@Schema(name = "ApiException", description = "Datos de error del sistema.")
public class ApiException extends RuntimeException{

    @JsonProperty
    @Schema(description = "Error message")
    private String message;

    @JsonIgnore
    @Schema(description = "Error code")
    private HttpStatus errorCategory;

    public ApiException(String message, HttpStatus errorCategory) {
        super(message);
        
        this.message = message;
        this.errorCategory = errorCategory;
    }
}
