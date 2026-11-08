package br.com.nircmi.controller;

import br.com.nircmi.model.Admission;
import br.com.nircmi.repository.AdmissionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * Equivalente ao endpoint GET /api/cmi/internacoes de server/index.js
 */
@Controller
public class AdmissionController {
    private static final Logger log = LoggerFactory.getLogger(AdmissionController.class);

    private final AdmissionRepository repository;

    public AdmissionController(AdmissionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/app/intenacao")
    public String appInteracao(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        return "forward:/app/internacao.html";
    }

    @GetMapping("/")
    public String root(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        return "forward:/app/internacao.html";
    }
}
