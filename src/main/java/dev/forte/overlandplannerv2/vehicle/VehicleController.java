package dev.forte.overlandplannerv2.vehicle;



import dev.forte.overlandplannerv2.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static dev.forte.overlandplannerv2.jwtconfig.AuthUtils.getAuthenticatedUserId;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/user/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getUserVehicles(Authentication authentication) {

        System.out.println("🔍 Vehicle request received");
        Long userId = getAuthenticatedUserId(authentication);

        System.out.println("✅ Authenticated User ID: " + userId);

        List<VehicleEntity> vehicles = vehicleService.getVehiclesByUser(userId);
        List<VehicleDTO> vehicleDTOS = vehicles.stream().map(VehicleDTO::new).toList();

        vehicles.forEach(vehicle -> System.out.println("🚗 Vehicle fetched: " + vehicle));

        return ResponseEntity.ok(vehicleDTOS);
    }

     @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleDTO> getUserVehicle(Authentication authentication,
                                                              @PathVariable Long vehicleId) {
        System.out.println("🔍 Vehicle request received");
        Long userId = getAuthenticatedUserId(authentication);

        System.out.println("✅ Authenticated User ID: " + userId);
        VehicleDTO vehicleDTO = vehicleService.getVehicleByUser(userId,vehicleId);

        return ResponseEntity.ok(vehicleDTO);
    }


    @PostMapping
    public ResponseEntity<List<VehicleDTO>> createVehicle( @RequestBody CreateVehicleDTO vehicleDTO,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        vehicleService.addVehicle(vehicleDTO, userId);
        List<VehicleEntity> userVehicles = vehicleService.getVehiclesByUser(userId);
        List<VehicleDTO> vehicleDTOS = userVehicles.stream().map(VehicleDTO::new).toList();
        return ResponseEntity.ok(vehicleDTOS);
    }

    @PutMapping("/{vehicleID}")
    public ResponseEntity<VehicleDTO> updateVehicleMods(Authentication authentication,@PathVariable Long vehicleID,
                                                    @RequestBody UpdateVehicleDTO updateVehicleDTO){

        Long userId = getAuthenticatedUserId(authentication);

        System.out.println("🔍 Update data received: " + updateVehicleDTO);

        VehicleDTO updatedVehicle = vehicleService.updateVehicleByUser(userId, vehicleID, updateVehicleDTO);
        return ResponseEntity.ok(updatedVehicle);

    }
}
