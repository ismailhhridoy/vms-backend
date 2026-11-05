package com.starkgroup.vms.vehicle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starkgroup.vms.vehicle.dto.VehicleCreateRequest;
import com.starkgroup.vms.vehicle.dto.VehicleResponse;
import com.starkgroup.vms.vehicle.dto.VehicleStatsResponse;
import com.starkgroup.vms.vehicle.dto.VehicleUpdateRequest;
import com.starkgroup.vms.vehicle.entity.VehicleStatus;
import com.starkgroup.vms.vehicle.entity.VehicleType;
import com.starkgroup.vms.vehicle.service.VehicleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService vehicleService;

    @Test
    @DisplayName("GET /vehicles returns list of vehicles wrapped in ApiResponse")
    void getAllVehicles_returnsOk() throws Exception {
        VehicleResponse vehicle = VehicleResponse.builder()
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

        when(vehicleService.getAllVehicles(null, null, null)).thenReturn(List.of(vehicle));

        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].vehicleNumber", is("SG-TRK-1042")))
                .andExpect(jsonPath("$.data[0].brand", is("Ford")));
    }

    @Test
    @DisplayName("GET /vehicles/{id} returns vehicle details")
    void getVehicleById_returnsOk() throws Exception {
        VehicleResponse vehicle = VehicleResponse.builder()
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

        when(vehicleService.getVehicleById(1L)).thenReturn(vehicle);

        mockMvc.perform(get("/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.vehicleNumber", is("SG-TRK-1042")));
    }

    @Test
    @DisplayName("POST /vehicles with valid payload returns 201 Created")
    void createVehicle_validPayload_returnsCreated() throws Exception {
        VehicleCreateRequest request = VehicleCreateRequest.builder()
                .vehicleNumber("SG-TRK-1042")
                .company("Stark Logistics")
                .type(VehicleType.TRUCK)
                .brand("Ford")
                .model("F-150 Lightning")
                .status(VehicleStatus.ACTIVE)
                .build();

        VehicleResponse response = VehicleResponse.builder()
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

        when(vehicleService.createVehicle(any(VehicleCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.vehicleNumber", is("SG-TRK-1042")));
    }

    @Test
    @DisplayName("POST /vehicles with invalid blank fields returns 400 Bad Request with field errors")
    void createVehicle_invalidPayload_returnsBadRequest() throws Exception {
        VehicleCreateRequest request = VehicleCreateRequest.builder()
                .vehicleNumber("") // blank
                .company("")
                .type(null)
                .brand("")
                .model("")
                .status(null)
                .build();

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Validation failed for one or more fields")));
    }

    @Test
    @DisplayName("PUT /vehicles/{id} with valid payload returns 200 OK")
    void updateVehicle_returnsOk() throws Exception {
        VehicleUpdateRequest request = VehicleUpdateRequest.builder()
                .vehicleNumber("SG-TRK-1042")
                .company("Stark Logistics Global")
                .type(VehicleType.TRUCK)
                .brand("Ford")
                .model("F-150 Lightning Pro")
                .status(VehicleStatus.MAINTENANCE)
                .build();

        VehicleResponse response = VehicleResponse.builder()
                .id(1L)
                .vehicleNumber("SG-TRK-1042")
                .company("Stark Logistics Global")
                .type(VehicleType.TRUCK)
                .brand("Ford")
                .model("F-150 Lightning Pro")
                .status(VehicleStatus.MAINTENANCE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(vehicleService.updateVehicle(eq(1L), any(VehicleUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.model", is("F-150 Lightning Pro")));
    }

    @Test
    @DisplayName("DELETE /vehicles/{id} returns 200 OK")
    void deleteVehicle_returnsOk() throws Exception {
        doNothing().when(vehicleService).deleteVehicle(1L);

        mockMvc.perform(delete("/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Vehicle deleted successfully")));
    }

    @Test
    @DisplayName("GET /vehicles/stats returns fleet metrics")
    void getStats_returnsOk() throws Exception {
        VehicleStatsResponse stats = VehicleStatsResponse.builder()
                .totalCount(10L)
                .activeCount(6L)
                .maintenanceCount(3L)
                .inactiveCount(1L)
                .build();

        when(vehicleService.getFleetStats()).thenReturn(stats);

        mockMvc.perform(get("/vehicles/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalCount", is(10)))
                .andExpect(jsonPath("$.data.activeCount", is(6)));
    }
}

