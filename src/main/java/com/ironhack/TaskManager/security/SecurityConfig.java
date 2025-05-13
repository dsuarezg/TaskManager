package com.ironhack.TaskManager.security;

import com.ironhack.TaskManager.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;


    /**
     * Configures the application's security filter chain, defining authentication and authorization rules for HTTP requests.
     *
     * <p>
     * Disables CSRF protection, sets session management to stateless, and specifies access permissions for various API endpoints based on user roles.
     * Integrates a JWT authentication filter before the standard username/password authentication filter.
     * </p>
     *
     * @param http the {@link HttpSecurity} to modify
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // For development. In production, configure appropriately
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Server doesn't store session state, because it's a REST API
                /*.authorizeHttpRequests(auth -> auth
                        // Public routes
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Routes protected by role
                        .requestMatchers("/api/user/**").hasRole("ADMIN")
                        //.requestMatchers("/api/task/**").hasAnyRole("ADMIN")
                        //Protected by User
                        //.requestMatchers("/api/task/**").authenticated()
                        .requestMatchers("/api/task/mandatory/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/task/personal/**").hasAnyRole("ADMIN", "USER")
                        // All other routes require authentication
                        .anyRequest().authenticated()
                )*/
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/auth/login").permitAll()
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

// Solo ADMIN puede gestionar usuarios
                                .requestMatchers("/api/user/**").hasRole("ADMIN")

// Personal Tasks
                                .requestMatchers("/api/task/personal/**").hasAnyRole("ADMIN", "USER")

// Mandatory Tasks
                                .requestMatchers("/api/task/mandatory/**").hasAnyRole("ADMIN", "USER", "MANAGER")

// Tareas generales (/task/** para completar/borrar tareas)
                                .requestMatchers("/api/task/**").hasAnyRole("ADMIN", "USER")

// Cualquier otra request
                                .anyRequest().authenticated()

                )

                // Add our filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

