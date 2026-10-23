package br.com.nircmi.controller;

import br.com.nircmi.model.User;
import br.com.nircmi.dto.UserRequest;
import br.com.nircmi.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para controle de acesso
 */
@RestController
public class AccessController {
    private static final Logger log = LoggerFactory.getLogger(AccessController.class);

    private static final UserRepository repository = new UserRepository();

    // access tokens and userId are both UUID but must never be the same value
    private static Map<String, UUID> activeUserTokens;

    public AccessController() {}

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
    // public ResponseEntity<?> registerUser() {
    //     try {
    //         List<Internacao> rows = repository.findCmiInternacoes();
    //         return ResponseEntity.ok(Map.of("data", rows));
    //     } catch (Exception e) {
    //         log.error("Erro ao buscar internacoes CMI", e);
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(Map.of("error", "Erro ao buscar internacoes CMI"));
    //     }
    // }

    // verifica dados e cria auth token
    // @GetMapping("/api/auth/login")

    // valida token do usuario

}
