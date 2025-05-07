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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtService.verifyToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtService.extractUsername(token);
            String roles = jwtService.extractRoles(token);

            Collection<GrantedAuthority> authorities = extractAuthorities(roles);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, token, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // Manejo de excepciones (puedes registrar el error o devolver una respuesta personalizada)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Collection<GrantedAuthority> extractAuthorities(String roles) {

        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        String rolesString = roles.replace("[", "").replace("]", "");
        String[] rolesArray = rolesString.split(",");

        return Arrays.stream(rolesArray)
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}