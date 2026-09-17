package br.com.healthtech.medplatform.dto.request;

import br.com.healthtech.medplatform.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
    @NotBlank(message = "Email field cannot be empty.")
    @Email(message = "Invalid Email format.")
    @Size(max = 100, message = "Email field must not exceed 100 characters.")
    String email,

    @NotBlank(message = "Password field cannot be empty.")
    @Size(min = 6, max = 32, message = "Password field must have between 6 and 32 characters.")
    String password,

    @NotNull(message = "Field must not be null")
    UserRole role
    )
{}
