package com.starkgroup.vms.vehicle.repository;

import com.starkgroup.vms.vehicle.entity.Vehicle;
import com.starkgroup.vms.vehicle.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByVehicleNumberIgnoreCase(String vehicleNumber);

    boolean existsByVehicleNumberIgnoreCaseAndIdNot(String vehicleNumber, Long id);

    Optional<Vehicle> findByVehicleNumberIgnoreCase(String vehicleNumber);

    long countByStatus(VehicleStatus status);

    @Query("SELECT DISTINCT v.company FROM Vehicle v WHERE v.company IS NOT NULL ORDER BY v.company ASC")
    List<String> findDistinctCompanies();

    @Query("SELECT v FROM Vehicle v WHERE " +
            "(:status IS NULL OR v.status = :status) AND " +
            "(:company IS NULL OR LOWER(v.company) = LOWER(:company)) AND " +
            "(:query IS NULL OR :query = '' OR " +
            "LOWER(v.vehicleNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.company) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.brand) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.model) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY v.createdAt DESC")
    List<Vehicle> searchAndFilter(
            @Param("query") String query,
            @Param("status") VehicleStatus status,
            @Param("company") String company
    );
}

