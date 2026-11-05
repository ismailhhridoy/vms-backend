package com.starkgroup.vms.vehicle.controller;

import com.starkgroup.vms.common.response.ApiResponse;
import com.starkgroup.vms.vehicle.dto.VehicleCreateRequest;
import com.starkgroup.vms.vehicle.dto.VehicleResponse;
import com.starkgroup.vms.vehicle.dto.VehicleStatsResponse;
import com.starkgroup.vms.vehicle.dto.VehicleUpdateRequest;
import com.starkgroup.vms.vehicle.entity.VehicleStatus;
import com.starkgroup.vms.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAllVehicles(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) String company
    ) {
        List<VehicleResponse> vehicles = vehicleService.getAllVehicles(search, status, company);
        return ResponseEntity.ok(ApiResponse.success(vehicles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleById(@PathVariable Long id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> createVehicle(
            @Valid @RequestBody VehicleCreateRequest request
    ) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleUpdateRequest request
    ) {
        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted successfully", null));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<VehicleStatsResponse>> getFleetStats() {
        VehicleStatsResponse stats = vehicleService.getFleetStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<String>>> getCompanies() {
        List<String> companies = vehicleService.getCompanies();
        return ResponseEntity.ok(ApiResponse.success(companies));
    }
}

