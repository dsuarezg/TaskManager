package com.ironhack.TaskManager.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserTest {

    @Test
    @DisplayName("Test getAuthoritiesAsStrings")
    void testGetAuthoritiesAsStrings() {
        User user = new User();
        user.setRole(ERole.ROLE_ADMIN);

        List<String> authorities = user.getAuthoritiesAsStrings();

        assertNotNull(authorities, "Authorities should not be null");
        assertEquals(1, authorities.size(), "Authorities size should be 1");
        assertEquals("ROLE_ADMIN", authorities.get(0), "Authority should be ROLE_ADMIN");
    }

    @Test
    @DisplayName("Test getAuthorities")
    void testJsonSerialization() throws JsonProcessingException {
        User user = new User();
        user.setUsername("TestUser");
        user.setPassword("TestPassword");
        user.setRole(ERole.ROLE_USER);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);

        assertTrue(json.contains("\"authorities\":[\"ROLE_USER\"]"), "JSON should contain authorities as strings");
    }

    @Test
    @DisplayName("Test JSON Deserialization")
    void testJsonDeserialization() throws JsonProcessingException {
        String json = """
                {
                    "username": "TestUser",
                    "password": "TestPassword",
                    "role": "ROLE_USER"
                }
                """;

        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(json, User.class);

        assertNotNull(user, "User should not be null");
        assertEquals("TestUser", user.getUsername(), "Username should match");
        assertEquals("TestPassword", user.getPassword(), "Password should match");
        assertEquals(ERole.ROLE_USER, user.getRole(), "Role should match");
    }
}