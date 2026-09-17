package br.com.healthtech.medplatform.service;

import br.com.healthtech.medplatform.domain.User;
import br.com.healthtech.medplatform.domain.enums.UserRole;
import br.com.healthtech.medplatform.dto.request.RegisterUserRequest;
import br.com.healthtech.medplatform.dto.response.UserAuthResponse;
import br.com.healthtech.medplatform.exception.throwables.ConflictException;
import br.com.healthtech.medplatform.exception.throwables.NotFoundException;
import br.com.healthtech.medplatform.repository.UserRepository;
import br.com.healthtech.medplatform.service.serviceclass.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("User Registration Flow")
    class RegisterFlow {

        @Test
        @DisplayName("Should register a new user successfully when email is unique")
        void register_Success() {
            // Arrange
            var request = new RegisterUserRequest("test@example.com", "securePassword123", UserRole.CLIENT);
            when(userRepository.existsByEmail(request.email())).thenReturn(false);

            // Act
            UserAuthResponse response = userService.registerUser(request);

            // Assert
            assertNotNull(response);
            assertEquals("User successfully registered", response.message());
            assertEquals(request.email(), response.email());

            // Verify that password was hashed and user was saved
            verify(userRepository, times(1)).save(argThat(user ->
                    user.getEmail().equals(request.email()) &&
                            !user.getPassword().equals(request.password()) &&
                            BCrypt.checkpw(request.password(), user.getPassword())
            ));
        }

        @Test
        @DisplayName("Should throw exception during registration when email already exists")
        void register_ThrowsException_WhenEmailExists() {
            // Arrange
            var request = new RegisterUserRequest("existing@example.com", "password123", UserRole.CLIENT);
            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            // Act & Assert
            ConflictException exception = assertThrows(ConflictException.class, () ->
                    userService.registerUser(request)
            );

            assertEquals("Email is already registered.", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("User Authentication Flow")
    class LoginFlow {

        @Test
        @DisplayName("Should authenticate successfully when credentials are valid")
        void login_Success() {
            // Arrange
            var rawPassword = "mySecretPassword";
            var hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
            var user = User.builder()
                    .id(1L)
                    .email("user@example.com")
                    .password(hashedPassword)
                    .build();

            var request = new RegisterUserRequest("user@example.com", rawPassword, UserRole.CLIENT);
            when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

            // Act
            UserAuthResponse response = userService.login(request);

            // Assert
            assertNotNull(response);
            assertEquals("User successfully logged in", response.message());
            assertEquals(request.email(), response.email());
        }

        @Test
        @DisplayName("Should throw exception during login when user is not found")
        void login_ThrowsException_WhenUserNotFound() {
            // Arrange
            var request = new RegisterUserRequest("unknown@example.com", "anyPassword", UserRole.CLIENT);
            when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(NotFoundException.class, () ->
                    userService.login(request)
            );

            assertEquals("Email not found.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception during login when password does not match")
        void login_ThrowsException_WhenPasswordIsIncorrect() {
            // Arrange
            var correctPassword = "correctPassword";
            var wrongPassword = "wrongPassword";
            var hashedPassword = BCrypt.hashpw(correctPassword, BCrypt.gensalt());
            var user = User.builder()
                    .id(1L)
                    .email("user@example.com")
                    .password(hashedPassword)
                    .build();

            var request = new RegisterUserRequest("user@example.com", wrongPassword, UserRole.CLIENT);
            when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    userService.login(request)
            );

            assertEquals("Invalid fields.", exception.getMessage());
        }
    }
}

