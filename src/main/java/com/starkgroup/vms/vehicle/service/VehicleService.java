package com.starkgroup.vms.vehicle.service;

import com.starkgroup.vms.vehicle.dto.VehicleCreateRequest;
import com.starkgroup.vms.vehicle.dto.VehicleResponse;
import com.starkgroup.vms.vehicle.dto.VehicleStatsResponse;
import com.starkgroup.vms.vehicle.dto.VehicleUpdateRequest;
import com.starkgroup.vms.vehicle.entity.VehicleStatus;

import java.util.List;

public interface VehicleService {

    List<VehicleResponse> getAllVehicles(String search, VehicleStatus status, String company);

    VehicleResponse getVehicleById(Long id);

    VehicleResponse createVehicle(VehicleCreateRequest request);

    VehicleResponse updateVehicle(Long id, VehicleUpdateRequest request);

    void deleteVehicle(Long id);

    VehicleStatsResponse getFleetStats();

    List<String> getCompanies();
}

