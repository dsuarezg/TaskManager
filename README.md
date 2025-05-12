<div style="text-align: center;">
  <img src="src/main/resources/static/banner.png" alt="TaskMan"></div>

# Task Manager API

## Descripción del proyecto

TaskMan es una API RESTful desarrollada con Java y Spring Boot que permite la gestión de tareas personales y obligatorias. El sistema incluye autenticación mediante JWT, control de acceso basado en roles (`ADMIN`, `USER`, `MANAGER`), y permite a los usuarios gestionar sus propias tareas de forma segura y eficiente.

Los usuarios pueden:
- Registrarse e iniciar sesión.
- Crear, listar, completar y eliminar sus propias tareas personales.
- Crear, listar y gestionar tareas obligatorias (Mandatory Tasks).
- Visualizar tareas de su propiedad (o según su rol en determinados casos).

---

## Diagrama de clases


<div style="text-align: center;">
  <img src="src/main/resources/static/img.png" alt="Diagrama de clases">
</div>


Existen dos clases principales, `Task` y `User`, que representan las entidades del sistema. La clase `Task` tiene dos subclases: `PersonalTask` y `MandatoryTask`, cada una con sus propias propiedades y métodos.

- `User`
    - id
    - username
    - password
    - role

  ---------
El proyecto sigue una estructura de herencia entre entidades:

- `Task` (superclase abstracta)
    - id
    - description
    - isFinished


- `PersonalTask` (subclase de Task)
    - …
    - duration
    - place
  

- `MandatoryTask` (subclase de Task)
    - …
    - assignedTo
    - startDate
    - dueDate
    - priority

Por último, la clase asociativa `UserTask` modela la relación entre los usuarios y las tareas.

- `UserTask` 
    - id
    - userId
    - taskId
    - comments




Relaciones:
- `User` 1---* `UserTask` (One-to-Many)


- `Task` 1---1 `UserTask` (One-to-One)


---

## Setup (Instalación local)

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/dsuarezg/TaskManager.git
   cd TaskManager
   ```

2. **Configurar base de datos (MySQL)**  

        Crear una base de datos llamada `task_manager` en MySQL.


3. **Configuración recomendada de application.properties**
   ```properties
    spring.application.name=task-manager
    
    spring.security.user.name=[tu_usuario]
    spring.security.user.password=[tu_contraseña]
    
    spring.datasource.url=jdbc:mysql://localhost:3306/task_manager
    spring.datasource.username=[tu_usuario]
    spring.datasource.password=[tu_contraseña]
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    
    spring.jpa.properties.hibernate.transaction.coordinator_class=jdbc
    spring.jpa.properties.hibernate.transaction.jta.platform=org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.open-in-view=false

   ```

4. **Construir el proyecto**
   ```bash
   mvn clean install
   ```

5. **Levantar el servidor**
   ```bash
   mvn spring-boot:run
   ```

6. **Acceder a Swagger (Documentación API)**
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

---

## Tecnologías usadas

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- JWT (JSON Web Tokens)
- MySQL
- Maven
- Swagger / OpenAPI 3
- Lombok
- Postman
- JavaScript (scripts de Postman)
- JUnit 5
- Mockito
- MockMvc
- cUrl (a través de Postman y Swagger)

---

## Controladores y estructura de rutas

| Controlador | Endpoints principales |
|:---|:---|
| `AuthController` | `POST /api/auth/login` |
| `UserController` | `GET /api/user/all` (solo ADMIN) |
| `TaskController` | `POST /api/task/personal/create`, `GET /api/task/personal/list`, `POST /api/task/mandatory/create`, `GET /api/task/mandatory/list`, `PATCH /api/task/complete/{id}`, `DELETE /api/task/delete/{id}` |

**Notas de permisos:**
- `ADMIN` puede gestionar usuarios y todas las tareas.
- `MANAGER` puede visualizar, cerrar y eliminar tareas obligatorias.
- `USER` puede gestionar sus propias tareas personales y obligatorias.


---

## Extra Links

- [Planificación ClickUp](https://sharing.clickup.com/90151157132/b/h/6-901511003926-2/cf885b5586b2831)
- [Presentación de Slides](//TODO) 
- [Colección de tests en Postman](https://documenter.getpostman.com/view/20702470/2sB2jAbThU) 


---

### Desarrollado por Daniel Suárez

![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/dsuarezg/TaskManager?utm_source=oss&utm_medium=github&utm_campaign=dsuarezg%2FTaskManager&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)
