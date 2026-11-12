package br.com.nircmi.controller;

import br.com.nircmi.model.User;
import br.com.nircmi.dto.UserRequest;
import br.com.nircmi.repository.UserRepository;
import br.com.nircmi.util.Crypto;
import br.com.nircmi.controller.TokenStore;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.io.IOException;

/**
 * Endpoints para controle de acesso
 */
@Controller
public class AccessController {
    private static final Logger log = LoggerFactory.getLogger(AccessController.class);

    private final UserRepository repository;
    private final TokenStore tokenStore;

    public AccessController(UserRepository repository, TokenStore tokenStore) {
        this.repository = repository;
        this.tokenStore = tokenStore;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "forward:/login.html";
    }

    // verifica dados e cria auth token
    @PostMapping("/api/auth/login")
    @ResponseBody
    public void login(@RequestBody UserRequest body, HttpServletResponse response) throws IOException {
        User user = repository.getUserByEmail(body.userEmail);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid email or password");
            return;
        }

        String hashedPassword = Crypto.hashSha256(body.userPassword);
        if (!hashedPassword.equals(user.userPassword)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid email or password");
            return;
        }

        UUID accessToken;
        String tokenString;
        do {
            accessToken = UUID.randomUUID();
            tokenString = accessToken.toString();
        } while (accessToken.equals(user.userID));

        tokenStore.addToken(tokenString, user.userID);

        // Using Spring's ResponseCookie builder
        ResponseCookie authCookie = ResponseCookie.from("auth_token", tokenString)
            .httpOnly(true)
            .secure(false) // Set to true if running over HTTPS in production
            .path("/")
            .maxAge(24 * 60 * 60) // 1 day expiration
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, authCookie.toString());

        // Perform server-side redirect based on user permissions
        if ((user.userPermissions & User.uTypeAdmin) == User.uTypeAdmin) {
            response.sendRedirect("/admin/panel");
        } else {
            response.sendRedirect("/app/internacao");
        }
    }

    @PostMapping("/api/auth/logout")
    @ResponseBody
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. Extract and invalidate the auth token from cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("auth_token".equals(cookie.getName())) {
                    tokenStore.removeToken(cookie.getValue());
                }
            }
        }

        // 2. Clear the auth cookie by setting maxAge to 0
        ResponseCookie clearCookie = ResponseCookie.from("auth_token", "")
            .httpOnly(true)
            .secure(false) // Match your environment settings
            .path("/")
            .maxAge(0) // Immediate expiration
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        // 3. Redirect back to the login page
        response.sendRedirect("/login");
    }
}
