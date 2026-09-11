package br.com.healthtech.medplatform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object representing a user registration payload.
 */

public record RegisterRequest (
    @NotBlank(message = "Email field cannot be empty.")
    @Email(message = "Invalid Email format.")
    @Size(max = 100, message = "Email field must not exceed 100 characters.")
    String email,

    @NotBlank(message = "Password field cannot be empty.")
    @Size(min = 6, max = 32, message = "Password field must have between 6 and 32 characters.")
    String password
    )
{}
