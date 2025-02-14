package dev.forte.overlandplannerv2.vehicle;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // Save a new vehicle for the user
    public void addVehicle(CreateVehicleDTO vehicleDTO, Long userId) {
        // Map DTO to Entity
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setMake(vehicleDTO.getMake());
        vehicleEntity.setModel(vehicleDTO.getModel());
        vehicleEntity.setYear(vehicleDTO.getYear());
        vehicleEntity.setModifications(vehicleDTO.getModifications());
        vehicleEntity.setUserId(userId); // 🔥 Store userId directly instead of the full UserEntity

        // Save to the database
        vehicleRepository.save(vehicleEntity);
    }

    // Get all vehicles for a specific user
    public List<VehicleEntity> getVehiclesByUser(Long userId) {

        System.out.println("🔍 Fetching vehicles for user ID: " + userId);

        List<VehicleEntity> vehicles = vehicleRepository.findByUserId(userId);

        if (vehicles.isEmpty()) {
                System.out.println("⚠ No vehicles found for user ID: " + userId);
        } else {
            System.out.println("✅ Found " + vehicles.size() + " vehicles for user ID: " + userId);
        }
        return vehicles;

    }

    public VehicleDTO getVehicleByUser(Long userId, Long vehicleId) {

        System.out.println("🔍 Fetching vehicle for user ID: " + userId + " and vehicle ID: " + vehicleId);

        VehicleEntity vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId);

        if (vehicle == null) {
            System.out.println("⚠ No vehicles found for user ID: " + userId + " and vehicle ID: " + vehicleId);
            throw new RuntimeException("Vehicle not found or access denied.");
        }

        System.out.println("Found vehicle: " + vehicle.toString());
        return new VehicleDTO(vehicle);

    }

    public VehicleDTO updateVehicleByUser(Long userId, Long vehicleId, UpdateVehicleDTO updateVehicleDTO) {

        // 🔍 Fetch the vehicle using the userId and vehicleId.
        VehicleEntity vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId);
        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found or access denied.");
        }

        // 🛠 Update only fields provided in the UpdateVehicleDTO, ignoring nulls.
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

        // 🔄 Save the updated vehicle.
        vehicleRepository.save(vehicle);

        System.out.println("✅ Updated vehicle for user ID: " + userId + " and vehicle ID: " + vehicleId);
        return new VehicleDTO(vehicle);  // Map the updated entity to a DTO and return.
    }

}
