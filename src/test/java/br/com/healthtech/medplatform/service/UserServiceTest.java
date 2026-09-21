package br.com.healthtech.medplatform.service;

import br.com.healthtech.medplatform.domain.User;
import br.com.healthtech.medplatform.domain.enums.UserRole;
import br.com.healthtech.medplatform.dto.request.LoginAuthRequest;
import br.com.healthtech.medplatform.dto.request.RegisterUserRequest;
import br.com.healthtech.medplatform.dto.response.UserAuthResponse;
import br.com.healthtech.medplatform.exception.throwables.ConflictException;
import br.com.healthtech.medplatform.exception.throwables.NotFoundException;
import br.com.healthtech.medplatform.repository.UserRepository;
import br.com.healthtech.medplatform.service.security.TokenService;
import br.com.healthtech.medplatform.service.serviceclass.UserService;
import org.junit.jupiter.api.BeforeEach;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    private User existingUser;
    private String rawPassword;

    @BeforeEach
    void setUp() {
        rawPassword = "securePassword123";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        existingUser = User.builder()
                .id(1L)
                .email("test@healthtech.com")
                .password(hashedPassword)
                .role(UserRole.CLIENT)
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user when email does not exist")
    void registerUser_Success() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest("new@healthtech.com", "password123", UserRole.CLIENT);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // Act
        UserAuthResponse response = userService.registerUser(request);

        // Assert
        assertNotNull(response);
        assertEquals("User successfully registered", response.message());
        assertEquals(request.email(), response.email());
        assertNull(response.token());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when registering a user with an already existing email")
    void registerUser_ThrowsConflictException() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest("test@healthtech.com", "password123", UserRole.CLIENT);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully log in and return a JWT token when credentials are valid")
    void login_Success() {
        // Arrange
        LoginAuthRequest request = new LoginAuthRequest("test@healthtech.com", rawPassword);
        String mockToken = "mocked-jwt-token-string";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser)); // Had to use anyString, since it was giving errors when specified
        when(tokenService.generateToken(anyString())).thenReturn(mockToken);

        // Act
        UserAuthResponse response = userService.login(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.message());
        assertEquals(existingUser.getEmail(), response.email());
        assertEquals(mockToken, response.token());
    }


    @Test
    @DisplayName("Should throw NotFoundException when user email is not found during login")
    void login_ThrowsNotFoundException() {
        // Arrange
        LoginAuthRequest request = new LoginAuthRequest("unknown@healthtech.com", "anyPassword");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.login(request));
        verify(tokenService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when password verification fails during login")
    void login_ThrowsIllegalArgumentException_OnInvalidPassword() {
        // Arrange
        LoginAuthRequest request = new LoginAuthRequest("test@healthtech.com", "wrongPassword");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.login(request));
        verify(tokenService, never()).generateToken(anyString());
    }
}


