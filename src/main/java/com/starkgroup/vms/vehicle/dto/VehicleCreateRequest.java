package com.starkgroup.vms.vehicle.dto;

import com.starkgroup.vms.vehicle.entity.VehicleStatus;
import com.starkgroup.vms.vehicle.entity.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCreateRequest {

    @NotBlank(message = "Vehicle number is required")
    @Size(min = 3, max = 50, message = "Vehicle number must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "Vehicle number must contain only letters, numbers, and hyphens")
    private String vehicleNumber;

    @NotBlank(message = "Company is required")
    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    private String company;

    @NotNull(message = "Vehicle type is required (e.g. SEDAN, SUV, TRUCK, VAN, ELECTRIC)")
    private VehicleType type;

    @NotBlank(message = "Brand / Manufacturer is required")
    @Size(max = 50, message = "Brand name cannot exceed 50 characters")
    private String brand;

    @NotBlank(message = "Model name is required")
    @Size(max = 50, message = "Model name cannot exceed 50 characters")
    private String model;

    @NotNull(message = "Status is required (e.g. ACTIVE, MAINTENANCE, INACTIVE)")
    private VehicleStatus status;
}

