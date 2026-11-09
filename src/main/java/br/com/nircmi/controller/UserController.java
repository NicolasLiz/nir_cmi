package br.com.nircmi.controller;

import br.com.nircmi.model.User;

import br.com.nircmi.repository.UserRepository;

import br.com.nircmi.util.Crypto;
import br.com.nircmi.dto.UserRequest;
import br.com.nircmi.security.RequiresPermission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para operações feitas pelo admin
 */
@Controller
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(AccessController.class);

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // registra novo usuario
    @PostMapping(value = "/api/register_user", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermRegisterUser)
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {
        try {
            User newUser = new User();
            newUser.userName = userRequest.userName;
            newUser.userEmail = userRequest.userEmail;
            newUser.userPassword = Crypto.hashSha256(userRequest.userPassword);
            newUser.userPermissions = userRequest.userPermissions;
            newUser.userActive = 1; // Active by default

            // 2. Attempt to create the user in the repository
            boolean created = userRepository.createUser(newUser);

            if (!created) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: Username or Email already exists.");
            }

            log.info("User registered successfully: {}", newUser.userName);
            return ResponseEntity.ok("User registered successfully: " + newUser.userName);

        } catch (Exception e) {
            log.error("Erro ao registrar usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while registering user.");
        }
    }

    // pega todos os usuarios
    @GetMapping(value = "/api/get_users")
    @ResponseBody
    @RequiresPermission(User.uPermViewUser)
    public ResponseEntity<?> getUsers() {
        try {
            List<User> users = userRepository.getAllUsers();

            if (users != null && !users.isEmpty()) {
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.ok("{}");
            }

        } catch (Exception e) {
            log.error("Erro ao buscar usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while getting user.");
        }
    }

    // used for editing user data including password
    // search user by name/email and sets the updated data to the new values
    @PostMapping(value = "/api/update_user", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermUpdateUser)
    public ResponseEntity<?> updateUser(@RequestBody UserRequest userRequest) {
        try {
            // 1. Find the existing user by ID
            User existingUser = userRepository.getUserByID(userRequest.userID);
            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: User not found.");
            }

            // 2. Update fields
            existingUser.userName = userRequest.userName;
            existingUser.userEmail = userRequest.userEmail;
            System.out.println(existingUser.userPermissions);
            System.out.println(userRequest.userPermissions);
            existingUser.userPermissions = userRequest.userPermissions;
            existingUser.userActive = userRequest.userActive; // Adjust if your entity uses boolean

            // 3. Only update the password if a new one was actually typed in the modal
            if (userRequest.userPassword != null && !userRequest.userPassword.trim().isEmpty()) {
                existingUser.userPassword = Crypto.hashSha256(userRequest.userPassword);
            }

            // 4. Save updates in the repository
            boolean updated = userRepository.updateUser(existingUser);

            if (!updated) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: Failed to update user.");
            }

            log.info("User updated successfully: {}", existingUser.userName);
            return ResponseEntity.ok("User updated successfully: " + existingUser.userName);

        } catch (Exception e) {
            log.error("Erro ao atualizar usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while updating user.");
        }
    }
}
