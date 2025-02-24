package dev.forte.overlandplannerv2;


import dev.forte.overlandplannerv2.vehicle.dtos.CreateVehicleDTO;
import dev.forte.overlandplannerv2.vehicle.entities.VehicleEntity;
import dev.forte.overlandplannerv2.vehicle.repositories.VehicleRepository;
import dev.forte.overlandplannerv2.vehicle.services.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VehicleServiceTest {

    private VehicleService vehicleService;
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        vehicleRepository = mock(VehicleRepository.class);
        vehicleService = new VehicleService(vehicleRepository);
    }

    @Test
    void testAddVehicle() {

        // Arrange
        CreateVehicleDTO vehicleDTO = new CreateVehicleDTO();
        vehicleDTO.setMake("Toyota");
        vehicleDTO.setModel("Tacoma");
        vehicleDTO.setYear(2022);
        vehicleDTO.setModifications("Lift kit");

        Long userId = 1L;

        // Captor to capture the entity saved in the repository
        ArgumentCaptor<VehicleEntity> vehicleCaptor = ArgumentCaptor.forClass(VehicleEntity.class);

        // Act
        vehicleService.addVehicle(vehicleDTO, userId);

        // Assert
        verify(vehicleRepository, times(1)).save(vehicleCaptor.capture()); // Verify 'save' was called
        VehicleEntity savedVehicle = vehicleCaptor.getValue();

        assertNotNull(savedVehicle);
        assertEquals("Toyota", savedVehicle.getMake());
        assertEquals("Tacoma", savedVehicle.getModel());
        assertEquals(2022, savedVehicle.getYear());
        assertEquals("Lift kit", savedVehicle.getModifications());
        assertEquals(userId, savedVehicle.getUserId());

    }

    @Test
    void testAddVehicleWithNullDTO() {
        assertThrows(NullPointerException.class, () -> vehicleService.addVehicle(null, 1L));
    }

}
