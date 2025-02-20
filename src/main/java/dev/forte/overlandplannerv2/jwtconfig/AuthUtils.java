package dev.forte.overlandplannerv2.jwtconfig;

import org.springframework.security.core.Authentication;
import dev.forte.overlandplannerv2.security.CustomUserDetails;

public class AuthUtils {

    private AuthUtils() {
        // Private constructor to prevent instantiation
    }

    public static Long getAuthenticatedUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("❌ Authentication failed - User is not logged in");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}
