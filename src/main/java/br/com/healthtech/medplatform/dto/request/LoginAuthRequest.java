package br.com.healthtech.medplatform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginAuthRequest(
        @NotBlank(message = "Email field cannot be empty.")
        @Email(message = "Invalid Email format.")
        String email,

        @NotBlank(message = "Password field cannot be empty.")
        String password)
{}

