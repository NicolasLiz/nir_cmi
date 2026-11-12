package br.com.nircmi.controller;

import br.com.nircmi.model.User;

import br.com.nircmi.model.Patient;

import br.com.nircmi.repository.PatientRepository;

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
public class PatientController {
    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }



    @PostMapping(value = "/api/register_patient", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermRegisterPatient)
    public ResponseEntity<?> registerPatient(@RequestBody Patient patientRequest) {
        try {
            Patient newPatient = new Patient();
            newPatient.patientName = patientRequest.patientName;
            newPatient.patientBirthDate = patientRequest.patientBirthDate;
            newPatient.patientDocument = patientRequest.patientDocument;
            newPatient.patientChart = patientRequest.patientChart;
            newPatient.patientRegisterDate = LocalDateTime.now();

            // 2. Attempt to create the user in the repository
            boolean created = patientRepository.createPatient(newPatient);

            if (!created) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: failed to create patient.");
            }

            log.info("patient created successfully: {}", newPatient.patientID);
            return ResponseEntity.ok("patient registered successfully: " + newPatient.patientID);

        } catch (Exception e) {
            log.error("Erro ao registrar paciente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while registering patient.");
        }
    }

    @GetMapping(value = "/api/get_patients")
    @ResponseBody
    @RequiresPermission(User.uPermViewPatient)
    public ResponseEntity<?> getPatients() {
        try {
            List<Patient> patients = patientRepository.getAllPatients();

            if (patients != null && !patients.isEmpty()) {
                return ResponseEntity.ok(patients);
            } else {
                return ResponseEntity.ok("{}");
            }

        } catch (Exception e) {
            log.error("Erro ao buscar pacientes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while getting patients.");
        }
    }
}
