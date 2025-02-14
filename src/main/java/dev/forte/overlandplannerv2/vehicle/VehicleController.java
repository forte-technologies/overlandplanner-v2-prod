package dev.forte.overlandplannerv2.vehicle;



import dev.forte.overlandplannerv2.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/user/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleEntity>> getUserVehicles(Authentication authentication) {
        try {
            System.out.println("🔍 Vehicle request received");

            if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
                throw new RuntimeException("❌ Authentication failed - User is not logged in");
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getId();
            System.out.println("✅ Authenticated User ID: " + userId);

            List<VehicleEntity> vehicles = vehicleService.getVehiclesByUser(userId);

            return ResponseEntity.ok(vehicles);
        } catch (Exception e) {
            System.err.println("❌ Error fetching vehicles: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }


    @PostMapping
    public ResponseEntity<List<VehicleEntity>> createVehicle(
            @RequestBody CreateVehicleDTO vehicleDTO,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        vehicleService.addVehicle(vehicleDTO, userId);
        List<VehicleEntity> userVehicles = vehicleService.getVehiclesByUser(userId);
        return ResponseEntity.ok(userVehicles);
    }
}
