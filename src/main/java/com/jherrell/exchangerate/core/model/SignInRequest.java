package com.jherrell.exchangerate.core.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9]+")
    @Size(min = 0, max = 50)
    private String username;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9]+")
    @Size(min = 0, max = 50)
    private String password;
}
