package dev.forte.overlandplannerv2.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/password")
public class PasswordResetController {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);
    
    private final PasswordResetService passwordResetService;
    
    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }
    
    @PostMapping("/forgot")
    public ResponseEntity<PasswordResetResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        logger.info("Received password reset request for email: {}", request.getEmail());
        
        try {
            passwordResetService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok(PasswordResetResponse.success(
                "If an account exists with that email, a password reset link has been sent."));
        } catch (Exception e) {
            logger.error("Error during password reset initiation", e);
            return ResponseEntity.badRequest().body(PasswordResetResponse.error(
                "An error occurred while processing your request. Please try again later."));
        }
    }
    
    @PostMapping("/reset")
    public ResponseEntity<PasswordResetResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        logger.info("Received request to reset password with token");
        
        try {
            passwordResetService.completePasswordReset(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(PasswordResetResponse.success("Your password has been successfully reset."));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid password reset attempt: {}", e.getMessage());
            return ResponseEntity.badRequest().body(PasswordResetResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error during password reset completion", e);
            return ResponseEntity.badRequest().body(PasswordResetResponse.error(
                "An error occurred while resetting your password. Please try again later."));
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<PasswordResetResponse> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(PasswordResetResponse.error("Token is required"));
        }
        
        try {
            boolean isValid = passwordResetService.validateToken(token);
            if (isValid) {
                return ResponseEntity.ok(PasswordResetResponse.success("Token is valid"));
            } else {
                return ResponseEntity.badRequest().body(PasswordResetResponse.error("Invalid or expired token"));
            }
        } catch (Exception e) {
            logger.error("Error validating token", e);
            return ResponseEntity.badRequest().body(PasswordResetResponse.error("Error validating token"));
        }
    }
} 