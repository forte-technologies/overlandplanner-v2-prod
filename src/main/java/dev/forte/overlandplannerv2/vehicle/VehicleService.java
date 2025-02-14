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
        try {
            System.out.println("🔍 Fetching vehicles for user ID: " + userId);

            List<VehicleEntity> vehicles = vehicleRepository.findByUserId(userId);

            if (vehicles.isEmpty()) {
                System.out.println("⚠ No vehicles found for user ID: " + userId);
            } else {
                System.out.println("✅ Found " + vehicles.size() + " vehicles for user ID: " + userId);
            }

            return vehicles;
        } catch (Exception e) {
            System.err.println("❌ Error in VehicleService: " + e.getMessage());
            e.printStackTrace();
            throw e; // ✅ Ensure error is logged and propagated
        }
    }

}
