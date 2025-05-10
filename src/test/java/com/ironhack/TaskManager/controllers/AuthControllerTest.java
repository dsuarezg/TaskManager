package com.ironhack.TaskManager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.TaskManager.models.ERole;
import com.ironhack.TaskManager.models.PersonalTask;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.repositories.UserRepository;
import com.ironhack.TaskManager.services.JwtService;
import com.ironhack.TaskManager.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User validUser;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Clear the database and set up a valid user before each test
        userRepository.deleteAll();

        validUser = new User();
        validUser.setUsername("ValidUser");
        validUser.setPassword("Valid1234"); // Plain password
        validUser.setRole(ERole.ROLE_USER);

        // Save the user using the service, which encrypts the password
        userService.createUser(validUser);
    }

    @AfterEach
    void tearDown() {
        // Clean up the database after each test
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Login successful returns 200")
    public void login_Success_ShouldReturn200() throws Exception {
        // Test case: Valid user credentials should return HTTP 200 and a token
        User loginRequest = new User();
        loginRequest.setUsername("ValidUser");
        loginRequest.setPassword("Valid1234"); // Plain password sent in the request

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(jsonPath("$.token").exists()); // Expect a token in the response
    }

    @Test
    @DisplayName("Authentication failure returns 401")
    public void login_Failure_ShouldReturn401() throws Exception {
        // Test case: Invalid password should return HTTP 401
        User loginRequest = new User();
        loginRequest.setUsername("ValidUser");
        loginRequest.setPassword("WrongPassword"); // Incorrect password

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()) // Expect HTTP 401
                .andExpect(jsonPath("$.message").value("Invalid username or password")); // Expect error message
    }

    @Test
    @DisplayName("User not found returns 404")
    public void login_UserNotFound_ShouldReturn404() throws Exception {
        // Test case: Non-existent user should return HTTP 404
        User loginRequest = new User();
        loginRequest.setUsername("NonExistent"); // Username not in the database
        loginRequest.setPassword("NoPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound()) // Expect HTTP 404
                .andExpect(jsonPath("$.message").value("User not found")); // Expect error message
    }

    @Test
    @DisplayName("Login successful returns a valid token")
    public void login_Success_ShouldReturnValidToken() throws Exception {
        // Test case: Valid user credentials should return HTTP 200 and a valid token
        User loginRequest = new User();
        loginRequest.setUsername("ValidUser");
        loginRequest.setPassword("Valid1234"); // Plain password sent in the request

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(jsonPath("$.token").exists()) // Expect a token in the response
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the token from the response
        String token = objectMapper.readTree(response).get("token").asText();

        // Decode and validate the token (using JwtService or a similar utility)
        String username = jwtService.extractUsername(token); // Replace with your JwtService method
        assertEquals("ValidUser", username); // Verify the username in the token
    }

    @Test
    @DisplayName("Create personal task returns 200")
    public void createPersonalTask_Success_ShouldReturn200() throws Exception {
        // Crear una tarea personal de prueba
        PersonalTask personalTask = new PersonalTask();
        personalTask.setDescription("Test personal task");
        personalTask.setDuration(60); // Duración en minutos
        personalTask.setPlace("Home"); // Establecer un valor válido para 'place'


        // Realizar login para obtener un token válido
        User loginRequest = new User();
        loginRequest.setUsername("ValidUser");
        loginRequest.setPassword("Valid1234");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extraer el token del JSON de respuesta
        String token = "Bearer " + objectMapper.readTree(response).get("token").asText();

        // Enviar solicitud POST para crear la tarea personal
        mockMvc.perform(post("/api/task/personal/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personalTask))
                        .header("Authorization", token)) // Incluir el token en el encabezado
                .andExpect(jsonPath("$.task.description").value("Test personal task")) // Verificar la descripción
                .andExpect(jsonPath("$.task.duration").value(60)) // Verificar la duración
                .andExpect(jsonPath("$.task.place").value("Home")); // Verificar el lugar
    }

    @Test
    @DisplayName("User who created the task can read it")
    public void userWhoCreatedTask_CanReadIt() throws Exception {
        // Crear una tarea personal de prueba
        PersonalTask personalTask = new PersonalTask();
        personalTask.setDescription("Test personal task");
        personalTask.setDuration(60);
        personalTask.setPlace("Home");

        // Realizar login para obtener un token válido
        User loginRequest = new User();
        loginRequest.setUsername("ValidUser");
        loginRequest.setPassword("Valid1234");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extraer el token del JSON de respuesta
        String token = "Bearer " + objectMapper.readTree(response).get("token").asText();

        // Crear la tarea personal
        mockMvc.perform(post("/api/task/personal/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personalTask))
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // Leer la tarea creada usando GET
        mockMvc.perform(get("/api/task/personal/list")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Test personal task"))
                .andExpect(jsonPath("$[0].duration").value(60))
                .andExpect(jsonPath("$[0].place").value("Home"));
    }

    @Test
    @DisplayName("Another user cannot read the task")
    public void anotherUser_CannotReadTask() throws Exception {
        // Crear una tarea personal de prueba
        PersonalTask personalTask = new PersonalTask();
        personalTask.setDescription("Test personal task");
        personalTask.setDuration(60);
        personalTask.setPlace("Home");

        // Realizar login para obtener un token válido del primer usuario
        User loginRequest = new User();
        loginRequest.setUsername("ValidUser");
        loginRequest.setPassword("Valid1234");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extraer el token del JSON de respuesta
        String token = "Bearer " + objectMapper.readTree(response).get("token").asText();
        System.out.println("Token del primer usuario: " + token);

        // Crear la tarea personal
        mockMvc.perform(post("/api/task/personal/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personalTask))
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // Crear un segundo usuario
        User secondUser = new User();
        secondUser.setUsername("SecondUser");
        secondUser.setPassword("Second1234");
        secondUser.setRole(ERole.ROLE_USER);
        userService.createUser(secondUser);

        // Realizar login para obtener un token válido del segundo usuario
        loginRequest.setUsername("SecondUser");
        loginRequest.setPassword("Second1234");

        response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extraer el token del segundo usuario
        String secondUserToken = "Bearer " + objectMapper.readTree(response).get("token").asText();
        System.out.println("Token del segundo usuario: " + secondUserToken);

        // Comprobar que los tokens son diferentes
        assertNotEquals(token, secondUserToken, "Los tokens deberían ser diferentes para cada usuario");

        // Intentar leer la tarea creada por el primer usuario
        mockMvc.perform(get("/api/task/personal/list")
                        .header("Authorization", secondUserToken))
                .andExpect(status().isForbidden()); // Esperar un error 403 Forbidden
    }
}