package dev.forte.overlandplannerv2.vehicle.repositories;

import dev.forte.overlandplannerv2.vehicle.entities.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
    List<VehicleEntity> findByUserId(Long userId);
    VehicleEntity findByIdAndUserId(Long id, Long userId);
// 🔥 Fetch vehicles directly by userId
}
