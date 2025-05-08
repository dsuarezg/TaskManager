package com.ironhack.TaskManager.filters;

import com.ironhack.TaskManager.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter that processes incoming HTTP requests to validate and authenticate JWT tokens.
 * It ensures that the user is authenticated and sets the security context for the request.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService; // Service to handle JWT-related operations.

    /**
     * Main method of the filter that intercepts each HTTP request.
     * @param request Incoming HTTP request.
     * @param response Outgoing HTTP response.
     * @param filterChain Filter chain to continue processing the request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Retrieve the "Authorization" header from the request.
        final String authHeader = request.getHeader("Authorization");

        // If the header is missing or does not start with "Bearer ", skip further processing.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT token by removing the "Bearer " prefix.
        final String token = authHeader.substring(7);

        // Verify the token's validity. If invalid, skip further processing.
        if (!jwtService.verifyToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the username and roles from the token.
        String username = jwtService.extractUsername(token);
        List<String> roles = jwtService.extractRoles(token);

        // Convert roles into a list of GrantedAuthority objects for Spring Security.
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Create an authentication token with the username and roles.
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        // Add additional request details to the authentication token.
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set the authentication token in the security context.
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Continue with the next filter in the chain.
        filterChain.doFilter(request, response);
    }
}