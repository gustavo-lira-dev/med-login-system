package br.com.healthtech.medplatform.service.serviceclass;

import br.com.healthtech.medplatform.domain.User;
import br.com.healthtech.medplatform.dto.request.RegisterUserRequest;
import br.com.healthtech.medplatform.dto.response.UserAuthResponse;
import br.com.healthtech.medplatform.exception.throwables.ConflictException;
import br.com.healthtech.medplatform.exception.throwables.InternalServerErrorException;
import br.com.healthtech.medplatform.exception.throwables.NotFoundException;
import br.com.healthtech.medplatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(rollbackOn = Exception.class)
    public UserAuthResponse registerUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email is already registered.");
        }
        User newUser = create(request);
        userRepository.save(newUser);
        return new UserAuthResponse("User successfully registered", request.email());
    }

    @Transactional(rollbackOn = Exception.class)
    public UserAuthResponse login(RegisterUserRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new NotFoundException("Email not found."));
        if (!BCrypt.checkpw(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid fields.");
        }
        return new UserAuthResponse("User successfully logged in", request.email());
    }

    private User create(RegisterUserRequest request) {
        String hashedPassword;
        try {
            hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        } catch (IllegalArgumentException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        return User.builder().email(request.email()).password(hashedPassword).role(request.role()).build();
    }
}
