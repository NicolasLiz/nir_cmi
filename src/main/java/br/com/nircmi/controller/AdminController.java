package br.com.nircmi.controller;

import br.com.nircmi.model.User;
import br.com.nircmi.model.Sector;

import br.com.nircmi.util.Crypto;
import br.com.nircmi.repository.UserRepository;
import br.com.nircmi.repository.SectorRepository;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para operações feitas pelo admin
 */
@Controller
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AccessController.class);

    private final UserRepository userRepository;
    private final SectorRepository sectorRepository;

    public AdminController(UserRepository userRepository, SectorRepository sectorRepository) {
        this.userRepository = userRepository;
        this.sectorRepository = sectorRepository;
    }

    private boolean isAdmin(User user) {
        return (user.userPermissions & User.uTypeAdmin) == User.uTypeAdmin;
    }

    @GetMapping("/admin/panel")
    public String adminPanel() {
        return "forward:/admin/panel.html";
    }

    // registra novo usuario
    @PostMapping(value = "/api/admin/register_user", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uTypeAdmin)
    public ResponseEntity<?> registerUser(@RequestAttribute("currentUser") User user, @RequestBody UserRequest userRequest) {
        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Forbidden: Insufficient permissions");
        }


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
    @GetMapping(value = "/api/admin/get_users")
    @ResponseBody
    @RequiresPermission(User.uTypeAdmin)
    public ResponseEntity<?> getUsers(@RequestAttribute("currentUser") User user) {
        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Forbidden: Insufficient permissions");
        }

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
    @PostMapping(value = "/api/admin/update_user", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uTypeAdmin)
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

    @PostMapping(value = "/api/admin/register_sector", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uTypeAdmin)
    public ResponseEntity<?> registerSector(@RequestAttribute("currentUser") User user, @RequestBody Sector sectorRequest) {
        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Forbidden: Insufficient permissions");
        }


        try {
            Sector newSector = new Sector();
            newSector.sectorName = sectorRequest.sectorName;
            newSector.sectorDescription = sectorRequest.sectorDescription;
            newSector.sectorActive = 1;

            // 2. Attempt to create the user in the repository
            boolean created = sectorRepository.createSector(newSector);

            if (!created) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: failed to create sector.");
            }

            log.info("sector created successfully: {}", newSector.sectorName);
            return ResponseEntity.ok("sector registered successfully: " + newSector.sectorName);

        } catch (Exception e) {
            log.error("Erro ao registrar setor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while registering sector.");
        }
    }

    @GetMapping(value = "/api/admin/get_sectors")
    @ResponseBody
    @RequiresPermission(User.uTypeAdmin)
    public ResponseEntity<?> getSectors(@RequestAttribute("currentUser") User user) {
        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Forbidden: Insufficient permissions");
        }

        try {
            List<Sector> sectors = sectorRepository.getAllSectors();

            if (sectors != null && !sectors.isEmpty()) {
                return ResponseEntity.ok(sectors);
            } else {
                return ResponseEntity.ok("{}");
            }

        } catch (Exception e) {
            log.error("Erro ao buscar setores", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while getting sectors.");
        }
    }

    @PostMapping(value = "/api/admin/update_sector", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uTypeAdmin)
    public ResponseEntity<?> updateSector(@RequestBody Sector sectorRequest) {
        try {
            // 1. Find the existing user by ID
            Sector existingSector = sectorRepository.getSectorByID(sectorRequest.sectorID);
            if (existingSector == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: Sector not found.");
            }

            // 2. Update fields
            existingSector.sectorName = sectorRequest.sectorName;
            existingSector.sectorDescription = sectorRequest.sectorDescription;
            existingSector.sectorActive = sectorRequest.sectorActive;

            // 4. Save updates in the repository
            boolean updated = sectorRepository.updateSector(existingSector);

            if (!updated) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: Failed to update sector.");
            }

            log.info("Sector updated successfully: {}", existingSector.sectorName);
            return ResponseEntity.ok("Sector updated successfully: " + existingSector.sectorName);

        } catch (Exception e) {
            log.error("Erro ao atualizar setor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while updating sector.");
        }
    }
}
