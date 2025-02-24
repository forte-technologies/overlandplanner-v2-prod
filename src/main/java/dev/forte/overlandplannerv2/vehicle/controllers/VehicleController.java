package dev.forte.overlandplannerv2.vehicle.controllers;


import dev.forte.overlandplannerv2.vehicle.dtos.CreateVehicleDTO;
import dev.forte.overlandplannerv2.vehicle.dtos.UpdateVehicleDTO;
import dev.forte.overlandplannerv2.vehicle.dtos.VehicleDTO;
import dev.forte.overlandplannerv2.vehicle.services.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static dev.forte.overlandplannerv2.jwtconfig.AuthUtils.getAuthenticatedUserId;

@Slf4j
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/user/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<VehicleDTO> getUserVehicles(Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);
        log.debug("Fetching vehicles for user ID: {}", userId);

        List<VehicleDTO> vehicleDTOs = vehicleService.getVehiclesByUser(userId);
        log.debug("Retrieved {} vehicles", vehicleDTOs.size());

        return vehicleDTOs;
    }

    @GetMapping("/{vehicleId}")
    public VehicleDTO getUserVehicle(Authentication authentication,
                                                     @PathVariable Long vehicleId) {

        Long userId = getAuthenticatedUserId(authentication);
        log.debug("Fetching vehicle ID: {} for user ID: {}", vehicleId, userId);

        return vehicleService.getVehicleByUser(userId, vehicleId);

    }

    @PostMapping
    public List<VehicleDTO> createVehicle(@RequestBody CreateVehicleDTO vehicleDTO,
                                                          Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);
        log.debug("Creating new vehicle for user ID: {}", userId);

        vehicleService.addVehicle(vehicleDTO, userId);
        List<VehicleDTO> vehicleDTOS = vehicleService.getVehiclesByUser(userId);
        log.debug("Vehicle created successfully for user ID: {}", userId);

        return vehicleDTOS;
    }

    @PutMapping("/{vehicleID}")
    public VehicleDTO updateVehicleMods(Authentication authentication, @PathVariable Long vehicleID,
                                                        @RequestBody UpdateVehicleDTO updateVehicleDTO) {

        Long userId = getAuthenticatedUserId(authentication);
        log.debug("Updating vehicle ID: {} for user ID: {}", vehicleID, userId);

        VehicleDTO updatedVehicle = vehicleService.updateVehicleByUser(userId, vehicleID, updateVehicleDTO);
        log.debug("Vehicle ID: {} updated successfully", vehicleID);

        return updatedVehicle;
    }

    @DeleteMapping("/{vehicleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserVehicle(
            @PathVariable Long vehicleId, Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);
        vehicleService.deleteVehicleByUser(userId, vehicleId);
        log.debug("Deleted vehicle with vehicle ID: {}", vehicleId);
    }
}