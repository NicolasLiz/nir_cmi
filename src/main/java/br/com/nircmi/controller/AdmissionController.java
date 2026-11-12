package br.com.nircmi.controller;

import br.com.nircmi.model.User;

import br.com.nircmi.model.Admission;
import br.com.nircmi.repository.AdmissionRepository;

import br.com.nircmi.util.Crypto;
import br.com.nircmi.dto.UserRequest;
import br.com.nircmi.security.RequiresPermission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestAttribute;
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

/**
 * Equivalente ao endpoint GET /api/cmi/internacoes de server/index.js
 */
@Controller
public class AdmissionController {
    private static final Logger log = LoggerFactory.getLogger(AdmissionController.class);

    private final AdmissionRepository admissionRepository;

    public AdmissionController(AdmissionRepository admissionRepository) {
        this.admissionRepository = admissionRepository;
    }

    @GetMapping("/app/internacao")
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



    @PostMapping(value = "/api/register_admission", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermInternPatient)
    public ResponseEntity<?> registerAdmission(@RequestAttribute(value = "currentUser", required = true) User user, @RequestBody Admission admissionRequest) {
        try {
            Admission newAdmission = new Admission();
            newAdmission.admissionPatientID = admissionRequest.admissionPatientID;
            newAdmission.admissionInDate = admissionRequest.admissionInDate;
            newAdmission.admissionOutDate = admissionRequest.admissionOutDate;
            newAdmission.admissionStatus = admissionRequest.admissionStatus;
            newAdmission.admissionObservation = admissionRequest.admissionObservation;
            newAdmission.admissionUserID = user.userID;

            // 2. Attempt to create the user in the repository
            boolean created = admissionRepository.createAdmission(newAdmission);

            if (!created) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: failed to create admission.");
            }

            log.info("admission created successfully: {}", newAdmission.admissionID);
            return ResponseEntity.ok("admission registered successfully: " + newAdmission.admissionID);

        } catch (Exception e) {
            log.error("Erro ao registrar internação", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal error while registering admission.");
        }
    }

    @GetMapping(value = "/api/get_admissions")
    @ResponseBody
    @RequiresPermission(User.uPermViewPatient)
    public ResponseEntity<?> getAdmissions() {
        try {
            List<Admission> admissions = admissionRepository.getAllAdmissions();

            if (admissions != null && !admissions.isEmpty()) {
                return ResponseEntity.ok(admissions);
            } else {
                return ResponseEntity.ok("{}");
            }

        } catch (Exception e) {
            log.error("Erro ao buscar internações", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while getting admissions.");
        }
    }

    @PostMapping(value = "/api/update_admission", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermInternPatient | User.uPermFreePatient)
    public ResponseEntity<?> updateAdmission(@RequestBody Admission admissionRequest) {
        try {
            // 1. Find the existing user by ID
            Admission existingAdmission = admissionRepository.getAdmissionByID(admissionRequest.admissionID);
            if (existingAdmission == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: Admission not found.");
            }

            // 2. Update fields
            existingAdmission.admissionStatus = admissionRequest.admissionStatus;
            existingAdmission.admissionObservation= admissionRequest.admissionObservation;

            // 4. Save updates in the repository
            boolean updated = admissionRepository.updateAdmission(existingAdmission);

            if (!updated) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: Failed to update admission.");
            }

            log.info("Admission updated successfully: {}", existingAdmission.admissionID);
            return ResponseEntity.ok("Admission updated successfully: " + existingAdmission.admissionID);

        } catch (Exception e) {
            log.error("Erro ao atualizar leito", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while updating admission.");
        }
    }
}
