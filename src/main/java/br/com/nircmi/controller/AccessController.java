package br.com.nircmi.controller;

import br.com.nircmi.model.User;
import br.com.nircmi.dto.UserRequest;
import br.com.nircmi.repository.UserRepository;
import br.com.nircmi.util.Crypto;
import br.com.nircmi.controller.TokenStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para controle de acesso
 */
@RestController
public class AccessController {
    private static final Logger log = LoggerFactory.getLogger(AccessController.class);

    private final UserRepository repository;
    private final TokenStore tokenStore;

    public AccessController(UserRepository repository, TokenStore tokenStore) {
        this.repository = repository;
        this.tokenStore = tokenStore;
    }

    // registra dados de usuario
    @PostMapping(value = "/api/auth/register", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody UserRequest user) {
        try {
            return ResponseEntity.ok(user.userName);
        } catch (Exception e) {
            log.error("Erro ao registrar usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao registrar usuario"));
        }
    }

    // verifica dados e cria auth token
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody UserRequest body) {
        User user = repository.getUserByEmail(body.userEmail);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }

        String hashedPassword = Crypto.hashSha256(body.userPassword);
        if (!hashedPassword.equals(user.userPassword)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }

        UUID accessToken;
        String tokenString;
        do {
            accessToken = UUID.randomUUID();
            tokenString = accessToken.toString();
        } while (accessToken.equals(user.userID));

        tokenStore.addToken(tokenString, user.userID);

        ResponseCookie authCookie = ResponseCookie.from("auth_token", tokenString)
            .httpOnly(true)
            .secure(false) // Set to true if running over HTTPS in production
            .path("/")
            .maxAge(24 * 60 * 60) // 1 day expiration
            .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                .header(HttpHeaders.LOCATION, "app/app.html")
                .build();
    }
}
