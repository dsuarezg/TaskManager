package com.ironhack.TaskManager;

import com.ironhack.TaskManager.models.ERole;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TaskManagerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private UserService userService;

	@Test
	public void createAndPersistUsers() {
		// Crear un usuario admin
		User admin = new User();
		admin.setUsername("Admin");
		admin.setPassword("Admin1234");
		admin.setRole(ERole.ROLE_ADMIN);

		// Crear un usuario manager
		User manager = new User();
		manager.setUsername("Manager");
		manager.setPassword("Manager1234");
		manager.setRole(ERole.ROLE_MANAGER);

		// Crear un usuario normal
		User user = new User();
		user.setUsername("User");
		user.setPassword("User1234");
		user.setRole(ERole.ROLE_USER);

		// Guardar los usuarios usando el servicio
		userService.createUser(admin);
		userService.createUser(manager);
		userService.createUser(user);

		// Verificar que los usuarios se hayan persistido correctamente
		Optional<User> persistedAdmin = userService.getByUsername("Admin");
		Optional<User> persistedManager = userService.getByUsername("Manager");
		Optional<User> persistedUser = userService.getByUsername("User");

		assertNotNull(persistedAdmin);
		assertEquals(ERole.ROLE_ADMIN, persistedAdmin.get().getRole());

		assertNotNull(persistedManager);
		assertEquals(ERole.ROLE_MANAGER, persistedManager.get().getRole());

		assertNotNull(persistedUser);
		assertEquals(ERole.ROLE_USER, persistedUser.get().getRole());
	}
}
