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

    private final SecurityFilter securityFilter;

    /// This method is basically the rules of how the API requisition flow will work, how and where it will be filtered.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error").permitAll() // Standard spring error endpoint also liberated

                        // Login and Register routes liberated
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/register").permitAll()

                        // Tests endpoints are liberated
                        .requestMatchers("/api/v1/test").permitAll()
                        .requestMatchers("/api/v1/test/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/test/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/test/**").permitAll()

                        // Route that needs authentication
                        .requestMatchers("/api/v1/appointments/**").authenticated()

                        // Any other route
                        .anyRequest().authenticated()
                )

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

