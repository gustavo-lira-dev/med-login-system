package br.com.healthtech.medplatform.controller;

import br.com.healthtech.medplatform.dto.request.RegisterUserRequest;
import br.com.healthtech.medplatform.dto.response.UserAuthResponse;
import br.com.healthtech.medplatform.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserService userService;

    @PostMapping
    @RequestMapping("/register")
    public ResponseEntity<UserAuthResponse> registerUser(@RequestBody @Valid RegisterUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerUser(request));
    }

    @PostMapping
    @RequestMapping("/login")
    public ResponseEntity<UserAuthResponse> login(@RequestBody @Valid RegisterUserRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.login(request));
    }

}
