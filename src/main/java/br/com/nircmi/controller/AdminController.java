package br.com.nircmi.controller;

import br.com.nircmi.model.User;

import br.com.nircmi.model.Sector;
import br.com.nircmi.model.Room;
import br.com.nircmi.model.Bed;

import br.com.nircmi.repository.UserRepository;
import br.com.nircmi.repository.SectorRepository;
import br.com.nircmi.repository.RoomRepository;
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
import org.springframework.web.bind.annotation.RequestAttribute;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para operações feitas pelo admin
 */
@Controller
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AccessController.class);

    public AdminController(UserRepository userRepository, SectorRepository sectorRepository, RoomRepository roomRepository, BedRepository bedRepository) {
    }

    @GetMapping("/admin/panel")
    public String adminPanel(@RequestAttribute(value = "currentUser", required = true) User user, HttpServletResponse response) {
        if (user.userPermissions != User.uTypeAdmin) {
            return "redirect:/login.html";
        }
        // Force the browser never to cache this page
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        return "forward:/admin/panel.html";
    }
}
