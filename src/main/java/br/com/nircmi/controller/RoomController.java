package br.com.nircmi.controller;

import br.com.nircmi.model.User;

import br.com.nircmi.model.Room;

import br.com.nircmi.repository.RoomRepository;

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
public class RoomController {
    private static final Logger log = LoggerFactory.getLogger(AccessController.class);

    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }



    @PostMapping(value = "/api/register_room", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermRegisterRoom)
    public ResponseEntity<?> registerRoom(@RequestBody Room roomRequest) {
        try {
            Room newRoom = new Room();
            newRoom.roomSectorID= roomRequest.roomSectorID;
            newRoom.roomIdentification = roomRequest.roomIdentification;
            newRoom.roomType = roomRequest.roomType;
            newRoom.roomActive = roomRequest.roomActive;

            // 2. Attempt to create the user in the repository
            boolean created = roomRepository.createRoom(newRoom);

            if (!created) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: failed to create room.");
            }

            log.info("room created successfully: {}", newRoom.roomID);
            return ResponseEntity.ok("room registered successfully: " + newRoom.roomID);

        } catch (Exception e) {
            log.error("Erro ao registrar setor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while registering room.");
        }
    }

    @GetMapping(value = "/api/get_rooms")
    @ResponseBody
    @RequiresPermission(User.uPermViewRoom)
    public ResponseEntity<?> getRooms() {
        try {
            List<Room> rooms = roomRepository.getAllRooms();

            if (rooms != null && !rooms.isEmpty()) {
                return ResponseEntity.ok(rooms);
            } else {
                return ResponseEntity.ok("{}");
            }

        } catch (Exception e) {
            log.error("Erro ao buscar quartos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while getting rooms.");
        }
    }

    @PostMapping(value = "/api/update_room", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @RequiresPermission(User.uPermUpdateRoom)
    public ResponseEntity<?> updateRoom(@RequestBody Room roomRequest) {
        try {
            // 1. Find the existing user by ID
            Room existingRoom = roomRepository.getRoomByID(roomRequest.roomID);
            if (existingRoom == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: Room not found.");
            }

            // 2. Update fields
            existingRoom.roomSectorID = roomRequest.roomSectorID;
            existingRoom.roomIdentification = roomRequest.roomIdentification;
            existingRoom.roomType = roomRequest.roomType;
            existingRoom.roomActive = roomRequest.roomActive;

            // 4. Save updates in the repository
            boolean updated = roomRepository.updateRoom(existingRoom);

            if (!updated) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: Failed to update room.");
            }

            log.info("Room updated successfully: {}", existingRoom.roomID);
            return ResponseEntity.ok("Room updated successfully: " + existingRoom.roomID);

        } catch (Exception e) {
            log.error("Erro ao atualizar quarto", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while updating room.");
        }
    }
}
