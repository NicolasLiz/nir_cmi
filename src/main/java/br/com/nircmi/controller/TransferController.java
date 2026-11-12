package br.com.nircmi.controller;

import br.com.nircmi.model.User;

import br.com.nircmi.model.Transfer;

import br.com.nircmi.repository.TransferRepository;

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
public class TransferController {
    private static final Logger log = LoggerFactory.getLogger(TransferController.class);

    private final TransferRepository transferRepository;

    public TransferController(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }



    @PostMapping(value = "/api/register_transfer", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermInternPatient)
    public ResponseEntity<?> registerTransfer(@RequestAttribute(value = "currentUser", required = true) User user, @RequestBody Transfer transferRequest) {
        try {
            Transfer newTransfer = new Transfer();
            newTransfer.transferAdmissionID = transferRequest.transferAdmissionID;
            newTransfer.transferFromBedID = transferRequest.transferFromBedID;
            newTransfer.transferToBedID = transferRequest.transferToBedID;
            newTransfer.transferDate = transferRequest.transferDate;
            newTransfer.transferReason = transferRequest.transferReason;

            newTransfer.transferUserID = user.userID;


            // 2. Attempt to create the user in the repository
            boolean created = transferRepository.createTransfer(newTransfer);

            if (!created) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: failed to create transfer.");
            }

            log.info("transfer created successfully: {}", newTransfer.transferID);
            return ResponseEntity.ok("transfer registered successfully: " + newTransfer.transferID);

        } catch (Exception e) {
            log.error("Erro ao registrar internação", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal error while registering transfer.");
        }
    }

    @GetMapping(value = "/api/get_transfers")
    @ResponseBody
    @RequiresPermission(User.uPermViewPatient)
    public ResponseEntity<?> getTransfers() {
        try {
            List<Transfer> transfers = transferRepository.getAllTransfers();

            if (transfers != null && !transfers.isEmpty()) {
                return ResponseEntity.ok(transfers);
            } else {
                return ResponseEntity.ok("{}");
            }

        } catch (Exception e) {
            log.error("Erro ao buscar internações", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while getting transfers.");
        }
    }
}
