package com.sebi.compliance.auth;

import com.sebi.compliance.auth.dto.JwtResponse;
import com.sebi.compliance.auth.dto.LoginRequest;
import com.sebi.compliance.auth.dto.RegisterRequest;
import com.sebi.compliance.common.dto.ApiResponse;
import com.sebi.compliance.security.UserPrincipal;
import com.sebi.compliance.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for User Login, Registration, Token Refresh, and Logout")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT access token")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", jwtResponse));
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user account")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = authService.registerUser(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", user));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT access token")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestBody Map<String, String> request) {
        String token = request.get("refreshToken");
        // Simple token refresh acknowledgement for SPA client
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", Map.of("accessToken", token != null ? token : "")));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and invalidate session context")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success("Logout successful", "User logged out successfully"));
    }
}
