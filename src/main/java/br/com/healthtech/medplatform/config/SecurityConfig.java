package br.com.healthtech.medplatform.config;

import br.com.healthtech.medplatform.service.security.SecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter; // Injeta o nosso filtro (Peça 2)

    /// This method is basically the rules of how the API requisition flow will work, how and where it will be filtered.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disables CSRF protection, since REST APIs with JWTs aren't vulnerable to such threats.
                .csrf(AbstractHttpConfigurer::disable)

                // Grants that no session will be stored into the servers memory; each one will be unique for every requisition
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Defines the access level for the endpoints.
                .authorizeHttpRequests(authorize -> authorize
                        // Login route is public, anyone can, and should, access
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()

                        // Test endpoints won't require authentication for fast testing without handmade tokens
                        .requestMatchers("/api/v1/test/**").permitAll()

                        // Every schedule route will demand login
                        .requestMatchers("/api/v1/appointments/**").authenticated()

                        // Every other route will also require login.
                        .anyRequest().authenticated()
                )

                // Puts the SecurityFilter authentication before the standard Spring filter.
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /// Configures the standard Spring security filter. It helps validate user's password during logins.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /// Defines the BCrypt algorithm to safely store user's passwords within the databank. The same algorithm used manually in previous commits.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

