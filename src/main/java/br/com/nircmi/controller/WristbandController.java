package br.com.nircmi.controller;

import br.com.nircmi.dto.PacienteWristbandRequest;
import br.com.nircmi.zebra.WristbandLabelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Equivalente ao endpoint POST /api/zebra/wristband de server/index.js
 */
@RestController
public class WristbandController {

    private static final Logger log = LoggerFactory.getLogger(WristbandController.class);

    private final WristbandLabelBuilder labelBuilder;

    public WristbandController(WristbandLabelBuilder labelBuilder) {
        this.labelBuilder = labelBuilder;
    }

    @PostMapping(value = "/api/zebra/wristband", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> generateWristband(@RequestBody PacienteWristbandRequest paciente) {
        try {
            String zpl = labelBuilder.build(paciente, paciente.getSettings());
            return ResponseEntity.ok(zpl);
        } catch (Exception e) {
            log.error("Erro ao gerar pulseira", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao gerar pulseira"));
        }
    }
}
