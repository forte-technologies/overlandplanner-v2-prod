package dev.forte.overlandplannerv2.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
    List<VehicleEntity> findByUserId(Long userId); // 🔥 Fetch vehicles directly by userId
}
