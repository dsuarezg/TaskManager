package com.ironhack.TaskManager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.TaskManager.models.ERole;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.repositories.UserRepository;
import com.ironhack.TaskManager.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        // Crear un usuario de prueba
        User user = new User();
        user.setUsername("UserTest");
        user.setPassword("Test1234");
        user.setRole(ERole.ROLE_USER);
        userService.createUser(user);

        // Realizar login para obtener un token real
        String loginRequest = objectMapper.writeValueAsString(user);
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extraer el token del JSON de respuesta
        token = "Bearer " + objectMapper.readTree(response).get("token").asText();
    }

    @Test
    @DisplayName("Autenticación exitosa devuelve 200 y un token")
    public void authentication_Success_ShouldReturn200AndToken() throws Exception {
        // Credenciales de prueba
        User loginRequest = new User();
        loginRequest.setUsername("UserTest");
        loginRequest.setPassword("Test1234");

        // Enviar solicitud POST al endpoint de login
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Verificar que el estado sea 200 OK
                .andExpect(jsonPath("$.token").exists()); // Verificar que el token esté presente en la respuesta
    }

    @Test
    @DisplayName("Autenticación fallida devuelve 401")
    public void authentication_Failure_ShouldReturn401() throws Exception {
        // Credenciales incorrectas
        User loginRequest = new User();
        loginRequest.setUsername("UserTest");
        loginRequest.setPassword("WrongPassword");

        // Enviar solicitud POST al endpoint de login
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()) // Verificar que el estado sea 401 Unauthorized
                .andExpect(jsonPath("$.message").value("Invalid username or password")); // Verificar el mensaje de error
    }

}