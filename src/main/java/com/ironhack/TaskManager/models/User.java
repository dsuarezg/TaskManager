package com.ironhack.TaskManager.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY) // Automatically generates unique IDs for each user
    private Long id;

    @NotBlank(message = "Username cannot be blank") // Ensures the username is not blank
    @Column(unique = true, nullable = false) // Enforces uniqueness and non-null constraint in the database
    String username;

    @NotBlank(message = "Password cannot be blank") // Ensures the password is not blank
    @Size(min = 8, message = "Password must be at least 8 characters long") // Validates minimum password length
    private String password;

    @Enumerated(EnumType.STRING) // Maps the enum to a string in the database
    private ERole role = ERole.ROLE_USER; // Default role is ROLE_USER

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Returns a collection of authorities (roles) for the user
        return List.of(new SimpleGrantedAuthority(role.name())); // Converts the role to a GrantedAuthority
    }

    @JsonProperty("authorities") // Exposes the authorities as a JSON property
    public List<String> getAuthoritiesAsStrings() {
        // Converts the authorities to a list of strings for easier JSON serialization
        return getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Maps each authority to its string representation
                .collect(Collectors.toList());
    }
}