package br.com.nircmi.controller;

import br.com.nircmi.model.User;

import br.com.nircmi.model.Sector;

import br.com.nircmi.repository.SectorRepository;

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
public class SectorController {
    private static final Logger log = LoggerFactory.getLogger(AccessController.class);

    private final SectorRepository sectorRepository;

    public SectorController(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }



    @PostMapping(value = "/api/register_sector", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermRegisterSector)
    public ResponseEntity<?> registerSector(@RequestBody Sector sectorRequest) {
        try {
            Sector newSector = new Sector();
            newSector.sectorName = sectorRequest.sectorName;
            newSector.sectorDescription = sectorRequest.sectorDescription;
            newSector.sectorActive = 1;

            // 2. Attempt to create the user in the repository
            boolean created = sectorRepository.createSector(newSector);

            if (!created) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: failed to create sector.");
            }

            log.info("sector created successfully: {}", newSector.sectorName);
            return ResponseEntity.ok("sector registered successfully: " + newSector.sectorName);

        } catch (Exception e) {
            log.error("Erro ao registrar setor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while registering sector.");
        }
    }


    @GetMapping(value = "/api/get_sectors")
    @ResponseBody
    @RequiresPermission(User.uPermViewSector)
    public ResponseEntity<?> getSectors() {
        try {
            List<Sector> sectors = sectorRepository.getAllSectors();

            if (sectors != null && !sectors.isEmpty()) {
                return ResponseEntity.ok(sectors);
            } else {
                return ResponseEntity.ok("{}");
            }

        } catch (Exception e) {
            log.error("Erro ao buscar setores", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while getting sectors.");
        }
    }

    @PostMapping(value = "/api/update_sector", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermUpdateSector)
    public ResponseEntity<?> updateSector(@RequestBody Sector sectorRequest) {
        try {
            // 1. Find the existing user by ID
            Sector existingSector = sectorRepository.getSectorByID(sectorRequest.sectorID);
            if (existingSector == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: Sector not found.");
            }

            // 2. Update fields
            existingSector.sectorName = sectorRequest.sectorName;
            existingSector.sectorDescription = sectorRequest.sectorDescription;
            existingSector.sectorActive = sectorRequest.sectorActive;

            // 4. Save updates in the repository
            boolean updated = sectorRepository.updateSector(existingSector);

            if (!updated) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: Failed to update sector.");
            }

            log.info("Sector updated successfully: {}", existingSector.sectorName);
            return ResponseEntity.ok("Sector updated successfully: " + existingSector.sectorName);

        } catch (Exception e) {
            log.error("Erro ao atualizar setor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while updating sector.");
        }
    }
}
