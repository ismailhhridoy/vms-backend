package com.starkgroup.vms.vehicle.dto;

import com.starkgroup.vms.vehicle.entity.Vehicle;
import com.starkgroup.vms.vehicle.entity.VehicleStatus;
import com.starkgroup.vms.vehicle.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {

    private Long id;
    private String vehicleNumber;
    private String company;
    private VehicleType type;
    private String brand;
    private String model;
    private VehicleStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public static VehicleResponse fromEntity(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .company(vehicle.getCompany())
                .type(vehicle.getType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .status(vehicle.getStatus())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}

