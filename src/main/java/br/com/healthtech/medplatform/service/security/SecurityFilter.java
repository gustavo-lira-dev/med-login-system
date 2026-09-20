package br.com.healthtech.medplatform.service.security;

import br.com.healthtech.medplatform.exception.throwables.NotFoundException;
import br.com.healthtech.medplatform.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter { // Grants the filter will be applied at least once in the non-public endpoints

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extracts token from request header
        String token = recoverToken(request);

        if (token != null) {
            // Uses TokenService class method to validate the token
            String email = tokenService.validateToken(token);

            if (email != null) {
                // Does a databank search using the email given by the authorization header
                UserDetails user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new NotFoundException("User not found"));

                // Creates a valid authentication object, with User's permission and data
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                // Delivers such object to Spring security; authenticating the data
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Sends the requisition ahead, to the next filter or controller
        filterChain.doFilter(request, response);
    }

    /**
     * Method for extracting the token from the request header.
     */
    private String recoverToken(HttpServletRequest request) {
        // Search the usual security dedicated Header: Authorization
        String authHeader = request.getHeader("Authorization");

        // Grants that the token is invalid if it doesn't start exactly with 'Bearer ' or it's empty
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        // Removes the 'Bearer ' part of the token, returning just a
        return authHeader.replace("Bearer ", "");
    }
}

