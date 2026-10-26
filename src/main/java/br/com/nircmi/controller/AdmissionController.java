package br.com.nircmi.controller;

import br.com.nircmi.model.Admission;
import br.com.nircmi.repository.AdmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Equivalente ao endpoint GET /api/cmi/internacoes de server/index.js
 */
@RestController
public class AdmissionController {
    private static final Logger log = LoggerFactory.getLogger(AdmissionController.class);

    private final AdmissionRepository repository;

    public AdmissionController(AdmissionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/cmi/admissions")
    public ResponseEntity<?> getAdmissions() {
        try {
            List<Admission> rows = repository.findCmiAdmissions();
            return ResponseEntity.ok(Map.of("data", rows));
        } catch (Exception e) {
            log.error("Erro ao buscar internacoes CMI", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao buscar internacoes CMI"));
        }
    }
}
