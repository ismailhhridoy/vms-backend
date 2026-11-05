package com.starkgroup.vms.vehicle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleStatsResponse {

    private long totalCount;
    private long activeCount;
    private long maintenanceCount;
    private long inactiveCount;
}

