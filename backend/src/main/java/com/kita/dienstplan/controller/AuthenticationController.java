package com.kita.dienstplan.controller;

import com.kita.dienstplan.entity.Admin;
import com.kita.dienstplan.repository.AdminRepository;
import com.kita.dienstplan.security.JwtService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Authentication Controller
 * Handles login and token generation
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AdminRepository adminRepository;

    /**
     * POST /api/auth/login
     * Authenticate admin and return JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Load admin details
            Admin admin = adminRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            // Update last login time
            admin.setLastLogin(LocalDateTime.now());
            adminRepository.save(admin);

            // Generate JWT token
            String token = jwtService.generateToken(admin);

            return ResponseEntity.ok(new AuthenticationResponse(
                    token,
                    admin.getUsername(),
                    admin.getFullName(),
                    "Login successful"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthenticationResponse(null, null, null, "Invalid credentials"));
        }
    }

    /**
     * GET /api/auth/me
     * Get current authenticated admin info
     */
    @GetMapping("/me")
    public ResponseEntity<AdminInfo> getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return ResponseEntity.ok(new AdminInfo(
                admin.getId(),
                admin.getUsername(),
                admin.getFullName(),
                admin.getEmail()
        ));
    }
}

/**
 * Login request DTO
 */
@Data
class LoginRequest {
    private String username;
    private String password;
}

/**
 * Authentication response DTO
 */
@Data
@lombok.AllArgsConstructor
class AuthenticationResponse {
    private String token;
    private String username;
    private String fullName;
    private String message;
}

/**
 * Admin info DTO
 */
@Data
@lombok.AllArgsConstructor
class AdminInfo {
    private Long id;
    private String username;
    private String fullName;
    private String email;
}
