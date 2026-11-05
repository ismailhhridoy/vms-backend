package com.starkgroup.vms.vehicle.service;

import com.starkgroup.vms.common.exception.DuplicateResourceException;
import com.starkgroup.vms.common.exception.ResourceNotFoundException;
import com.starkgroup.vms.vehicle.dto.VehicleCreateRequest;
import com.starkgroup.vms.vehicle.dto.VehicleResponse;
import com.starkgroup.vms.vehicle.dto.VehicleStatsResponse;
import com.starkgroup.vms.vehicle.dto.VehicleUpdateRequest;
import com.starkgroup.vms.vehicle.entity.Vehicle;
import com.starkgroup.vms.vehicle.entity.VehicleStatus;
import com.starkgroup.vms.vehicle.entity.VehicleType;
import com.starkgroup.vms.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle sampleVehicle;

    @BeforeEach
    void setUp() {
        sampleVehicle = Vehicle.builder()
                .id(1L)
                .vehicleNumber("SG-TRK-1042")
                .company("Stark Logistics")
                .type(VehicleType.TRUCK)
                .brand("Ford")
                .model("F-150 Lightning")
                .status(VehicleStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should successfully create a new vehicle with normalized uppercase vehicle number")
    void createVehicle_success() {
        VehicleCreateRequest request = VehicleCreateRequest.builder()
                .vehicleNumber("sg-trk-1042")
                .company("Stark Logistics")
                .type(VehicleType.TRUCK)
                .brand("Ford")
                .model("F-150 Lightning")
                .status(VehicleStatus.ACTIVE)
                .build();

        when(vehicleRepository.existsByVehicleNumberIgnoreCase("SG-TRK-1042")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(sampleVehicle);

        VehicleResponse response = vehicleService.createVehicle(request);

        assertThat(response).isNotNull();
        assertThat(response.getVehicleNumber()).isEqualTo("SG-TRK-1042");
        assertThat(response.getCompany()).isEqualTo("Stark Logistics");
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when vehicle number already exists")
    void createVehicle_duplicateNumber_throwsException() {
        VehicleCreateRequest request = VehicleCreateRequest.builder()
                .vehicleNumber("SG-TRK-1042")
                .company("Stark Logistics")
                .type(VehicleType.TRUCK)
                .brand("Ford")
                .model("F-150 Lightning")
                .status(VehicleStatus.ACTIVE)
                .build();

        when(vehicleRepository.existsByVehicleNumberIgnoreCase("SG-TRK-1042")).thenReturn(true);

        assertThatThrownBy(() -> vehicleService.createVehicle(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Vehicle already exists");

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should return vehicle details when existing ID is queried")
    void getVehicleById_success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));

        VehicleResponse response = vehicleService.getVehicleById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getBrand()).isEqualTo("Ford");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when non-existent ID is queried")
    void getVehicleById_notFound_throwsException() {
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.getVehicleById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle not found");
    }

    @Test
    @DisplayName("Should update existing vehicle successfully")
    void updateVehicle_success() {
        VehicleUpdateRequest request = VehicleUpdateRequest.builder()
                .vehicleNumber("SG-TRK-1042")
                .company("Stark Logistics Global")
                .type(VehicleType.TRUCK)
                .brand("Ford")
                .model("F-150 Lightning Pro")
                .status(VehicleStatus.MAINTENANCE)
                .build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));
        when(vehicleRepository.existsByVehicleNumberIgnoreCaseAndIdNot("SG-TRK-1042", 1L)).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(sampleVehicle);

        VehicleResponse response = vehicleService.updateVehicle(1L, request);

        assertThat(response).isNotNull();
        verify(vehicleRepository).save(sampleVehicle);
    }

    @Test
    @DisplayName("Should delete existing vehicle successfully")
    void deleteVehicle_success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository).delete(sampleVehicle);
    }

    @Test
    @DisplayName("Should calculate correct fleet statistics")
    void getFleetStats_success() {
        when(vehicleRepository.count()).thenReturn(10L);
        when(vehicleRepository.countByStatus(VehicleStatus.ACTIVE)).thenReturn(6L);
        when(vehicleRepository.countByStatus(VehicleStatus.MAINTENANCE)).thenReturn(3L);
        when(vehicleRepository.countByStatus(VehicleStatus.INACTIVE)).thenReturn(1L);

        VehicleStatsResponse stats = vehicleService.getFleetStats();

        assertThat(stats.getTotalCount()).isEqualTo(10L);
        assertThat(stats.getActiveCount()).isEqualTo(6L);
        assertThat(stats.getMaintenanceCount()).isEqualTo(3L);
        assertThat(stats.getInactiveCount()).isEqualTo(1L);
    }
}

