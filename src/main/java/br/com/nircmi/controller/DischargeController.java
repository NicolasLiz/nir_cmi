package br.com.nircmi.controller;

import br.com.nircmi.model.User;

import br.com.nircmi.model.Discharge;
import br.com.nircmi.repository.DischargeRepository;

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
import java.time.LocalDateTime;

/**
 * Endpoints para operações feitas pelo admin
 */
@Controller
public class DischargeController {
    private static final Logger log = LoggerFactory.getLogger(AccessController.class);

    private final DischargeRepository dischargeRepository;

    public DischargeController(DischargeRepository dischargeRepository) {
        this.dischargeRepository = dischargeRepository;
    }



    @PostMapping(value = "/api/register_discharge", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermFreePatient)
    public ResponseEntity<?> registerDischarge(@RequestAttribute(value = "currentUser", required = true) User user, @RequestBody Discharge dischargeRequest) {
        try {
            Discharge newDischarge = new Discharge();
            newDischarge.dischargePatientID = dischargeRequest.dischargePatientID;
            newDischarge.dischargeDate = dischargeRequest.dischargeDate;
            newDischarge.dischargeUserID = user.userID;

            // 2. Attempt to create the user in the repository
            boolean created = dischargeRepository.createDischarge(newDischarge);

            if (!created) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: failed to create discharge.");
            }

            log.info("discharge created successfully: {}", newDischarge.dischargeID);
            return ResponseEntity.ok("discharge registered successfully: " + newDischarge.dischargeID);

        } catch (Exception e) {
            log.error("Erro ao registrar leito", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while registering discharge.");
        }
    }

    @GetMapping(value = "/api/get_discharges")
    @ResponseBody
    @RequiresPermission(User.uPermViewPatient)
    public ResponseEntity<?> getDischarges() {
        try {
            List<Discharge> discharges = dischargeRepository.getAllDischarges();

            if (discharges != null && !discharges.isEmpty()) {
                return ResponseEntity.ok(discharges);
            } else {
                return ResponseEntity.ok("{}");
            }

        } catch (Exception e) {
            log.error("Erro ao buscar leitos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while getting discharges.");
        }
    }
}
