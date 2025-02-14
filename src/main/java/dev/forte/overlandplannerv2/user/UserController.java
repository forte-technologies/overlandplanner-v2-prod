package dev.forte.overlandplannerv2.user;

import dev.forte.overlandplannerv2.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/auth")
    public ResponseEntity<?> authenticateUser(Authentication authentication) {
        try {
            System.out.println("🔍 Auth Request Received");

            if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
                throw new RuntimeException("❌ Authentication object is null or anonymous");
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            System.out.println("✅ Authenticated User: " + userDetails.getUsername());

            return ResponseEntity.ok(Map.of(
                    "username", userDetails.getUsername(),
                    "message", "Authentication successful"
            ));
        } catch (Exception e) {
            System.err.println("❌ Error in authentication: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }
}
