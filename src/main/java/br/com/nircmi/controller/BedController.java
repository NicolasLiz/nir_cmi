package br.com.nircmi.controller;

import br.com.nircmi.model.User;

import br.com.nircmi.model.Bed;

import br.com.nircmi.repository.BedRepository;

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

/**
 * Endpoints para operações feitas pelo admin
 */
@Controller
public class BedController {
    private static final Logger log = LoggerFactory.getLogger(AccessController.class);

    private final BedRepository bedRepository;

    public BedController(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }



    @PostMapping(value = "/api/register_bed", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermRegisterBed)
    public ResponseEntity<?> registerBed(@RequestBody Bed bedRequest) {
        try {
            Bed newBed = new Bed();
            newBed.bedRoomID = bedRequest.bedRoomID;
            newBed.bedNumber = bedRequest.bedNumber;
            newBed.bedType = bedRequest.bedType;
            newBed.bedStatus = bedRequest.bedStatus;
            newBed.bedObservation = bedRequest.bedObservation;
            newBed.bedRegisterDate = bedRequest.bedRegisterDate;
            newBed.bedActive = bedRequest.bedActive;

            // 2. Attempt to create the user in the repository
            boolean created = bedRepository.createBed(newBed);

            if (!created) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: failed to create bed.");
            }

            log.info("bed created successfully: {}", newBed.bedID);
            return ResponseEntity.ok("bed registered successfully: " + newBed.bedID);

        } catch (Exception e) {
            log.error("Erro ao registrar leito", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while registering bed.");
        }
    }

    @GetMapping(value = "/api/get_beds")
    @ResponseBody
    @RequiresPermission(User.uPermViewBed)
    public ResponseEntity<?> getBeds() {
        try {
            List<Bed> beds = bedRepository.getAllBeds();

            if (beds != null && !beds.isEmpty()) {
                return ResponseEntity.ok(beds);
            } else {
                return ResponseEntity.ok("{}");
            }

        } catch (Exception e) {
            log.error("Erro ao buscar leitos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while getting beds.");
        }
    }

    @PostMapping(value = "/api/update_bed", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermUpdateBed)
    public ResponseEntity<?> updateBed(@RequestBody Bed bedRequest) {
        try {
            // 1. Find the existing user by ID
            Bed existingBed = bedRepository.getBedByID(bedRequest.bedID);
            if (existingBed == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: Bed not found.");
            }

            // 2. Update fields
            existingBed.bedRoomID = bedRequest.bedRoomID;
            existingBed.bedNumber = bedRequest.bedNumber;
            existingBed.bedType = bedRequest.bedType;
            existingBed.bedStatus = bedRequest.bedStatus;
            existingBed.bedObservation = bedRequest.bedObservation;
            existingBed.bedRegisterDate = bedRequest.bedRegisterDate;
            existingBed.bedActive = bedRequest.bedActive;

            // 4. Save updates in the repository
            boolean updated = bedRepository.updateBed(existingBed);

            if (!updated) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: Failed to update bed.");
            }

            log.info("Bed updated successfully: {}", existingBed.bedID);
            return ResponseEntity.ok("Bed updated successfully: " + existingBed.bedID);

        } catch (Exception e) {
            log.error("Erro ao atualizar leito", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while updating bed.");
        }
    }
}
