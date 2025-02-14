package dev.forte.overlandplannerv2.security;
import dev.forte.overlandplannerv2.jwtconfig.AuthResponse;
import dev.forte.overlandplannerv2.jwtconfig.JwtUtil;
import dev.forte.overlandplannerv2.jwtconfig.RefreshRequest;
import dev.forte.overlandplannerv2.jwtconfig.RefreshTokenService;
import dev.forte.overlandplannerv2.user.UserEntity;
import dev.forte.overlandplannerv2.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          RefreshTokenService refreshTokenService,
                          PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtUtil.generateToken(userDetails.getUsername());
            String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "username", userDetails.getUsername(),
                    "message", "Login successful"
            ));

        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage()); // ✅ Log the exact error
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            // Return an error message in JSON format
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Username is already taken!"));
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // Hash password before saving
        userRepository.save(user);

        // Return a success message in JSON format
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        System.out.println("Received refresh request with token: " + refreshRequest.getRefreshToken());

        String username = refreshTokenService.validateRefreshToken(refreshRequest.getRefreshToken());

        if (username == null) {
            System.out.println("Invalid refresh token!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, null, "Invalid refresh token"));
        }

        System.out.println("Valid refresh token for user: " + username);

        String newAccessToken = jwtUtil.generateToken(username);
        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshRequest.getRefreshToken(), username, "Token refreshed successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequest logoutRequest) {
        System.out.println("Logging out user with refresh token: " + logoutRequest.getRefreshToken());

        String username = refreshTokenService.getUsernameFromToken(logoutRequest.getRefreshToken());

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        refreshTokenService.revokeAllTokensForUser(username);

        return ResponseEntity.ok("User logged out successfully!");
    }





}
