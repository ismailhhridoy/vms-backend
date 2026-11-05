package com.starkgroup.vms.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "vehicles",
        indexes = {
                @Index(name = "idx_vehicle_number", columnList = "vehicle_number", unique = true),
                @Index(name = "idx_vehicle_company", columnList = "company"),
                @Index(name = "idx_vehicle_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_number", nullable = false, unique = true, length = 50)
    private String vehicleNumber;

    @Column(name = "company", nullable = false, length = 100)
    private String company;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private VehicleType type;

    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private VehicleStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.vehicleNumber != null) {
            this.vehicleNumber = this.vehicleNumber.trim().toUpperCase();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        if (this.vehicleNumber != null) {
            this.vehicleNumber = this.vehicleNumber.trim().toUpperCase();
        }
    }
}

