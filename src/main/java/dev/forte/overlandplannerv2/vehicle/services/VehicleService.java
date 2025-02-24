package dev.forte.overlandplannerv2.vehicle.services;

import dev.forte.overlandplannerv2.error.VehicleNotFoundException;
import dev.forte.overlandplannerv2.vehicle.dtos.CreateVehicleDTO;
import dev.forte.overlandplannerv2.vehicle.dtos.UpdateVehicleDTO;
import dev.forte.overlandplannerv2.vehicle.dtos.VehicleDTO;
import dev.forte.overlandplannerv2.vehicle.entities.VehicleEntity;
import dev.forte.overlandplannerv2.vehicle.repositories.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public void addVehicle(CreateVehicleDTO vehicleDTO, Long userId) {

        Objects.requireNonNull(vehicleDTO, "vehicleDTO cannot be null");
        Objects.requireNonNull(userId, "userId cannot be null");

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setMake(vehicleDTO.getMake());
        vehicleEntity.setModel(vehicleDTO.getModel());
        vehicleEntity.setYear(vehicleDTO.getYear());
        vehicleEntity.setModifications(vehicleDTO.getModifications());
        vehicleEntity.setUserId(userId);

        vehicleRepository.save(vehicleEntity);
        log.debug("Vehicle created successfully.");
    }

    public List<VehicleDTO> getVehiclesByUser(Long userId) {
        Objects.requireNonNull(userId, "userId cannot be null");
        log.debug("Fetching vehicles from repository for user {}", userId);
        List<VehicleEntity> vehicles = vehicleRepository.findByUserId(userId);

        if (vehicles.isEmpty()) {
            log.info("No vehicles found for user {}", userId);
        }
        log.debug("Retrieved {} vehicles for user {}", vehicles.size(), userId);
        return vehicles.stream().map(VehicleDTO::new).toList();
    }

    public VehicleDTO getVehicleByUser(Long userId, Long vehicleId) {
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(vehicleId, "vehicleId cannot be null");
        log.debug("Fetching vehicle for user ID: {} and vehicle ID: {}", userId, vehicleId);
        VehicleEntity vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId);

        if (vehicle == null) {
            log.error("No vehicle found for user ID: {} and vehicle ID: {}", userId, vehicleId);
            throw new VehicleNotFoundException("Vehicle not found or access denied.");
        }
        log.debug("Found vehicle: {}", vehicle);
        return new VehicleDTO(vehicle);
    }

    public VehicleDTO updateVehicleByUser(Long userId, Long vehicleId, UpdateVehicleDTO updateVehicleDTO) {
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(vehicleId, "vehicleId cannot be null");
        Objects.requireNonNull(updateVehicleDTO, "updateVehicleDTO cannot be null");

        VehicleEntity vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId);
        if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle not found or access denied.");
        }

        if (updateVehicleDTO.getMake() != null) {
            vehicle.setMake(updateVehicleDTO.getMake());
        }
        if (updateVehicleDTO.getModel() != null) {
            vehicle.setModel(updateVehicleDTO.getModel());
        }
        if (updateVehicleDTO.getYear() != null) {
            vehicle.setYear(updateVehicleDTO.getYear());
        }
        if (updateVehicleDTO.getModifications() != null) {
            vehicle.setModifications(updateVehicleDTO.getModifications());
        }
        vehicleRepository.save(vehicle);
        log.debug("Vehicle updated successfully with ID {}", vehicleId);
        return new VehicleDTO(vehicle);
    }

    public void deleteVehicleByUser(Long userId, Long vehicleId) {
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(vehicleId, "vehicleId cannot be null");
        VehicleEntity vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId);
        if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle not found or access denied.");
        }
        vehicleRepository.delete(vehicle);
        log.debug("Vehicle deleted successfully.");
    }



}
