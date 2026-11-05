package com.starkgroup.vms.vehicle.service;

import com.starkgroup.vms.common.exception.DuplicateResourceException;
import com.starkgroup.vms.common.exception.ResourceNotFoundException;
import com.starkgroup.vms.vehicle.dto.VehicleCreateRequest;
import com.starkgroup.vms.vehicle.dto.VehicleResponse;
import com.starkgroup.vms.vehicle.dto.VehicleStatsResponse;
import com.starkgroup.vms.vehicle.dto.VehicleUpdateRequest;
import com.starkgroup.vms.vehicle.entity.Vehicle;
import com.starkgroup.vms.vehicle.entity.VehicleStatus;
import com.starkgroup.vms.vehicle.repository.VehicleRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles(String search, VehicleStatus status, String company) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String comp = (company != null && !company.trim().isEmpty()) ? company.trim() : null;

        Specification<Vehicle> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (comp != null) {
                predicates.add(cb.equal(cb.lower(root.get("company")), comp.toLowerCase()));
            }

            if (query != null) {
                String pattern = "%" + query.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("vehicleNumber")), pattern),
                        cb.like(cb.lower(root.get("company")), pattern),
                        cb.like(cb.lower(root.get("brand")), pattern),
                        cb.like(cb.lower(root.get("model")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Vehicle> vehicles = vehicleRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
        return vehicles.stream()
                .map(VehicleResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        return VehicleResponse.fromEntity(vehicle);
    }

    @Override
    @Transactional
    public VehicleResponse createVehicle(VehicleCreateRequest request) {
        String normalizedNumber = request.getVehicleNumber().trim().toUpperCase();

        if (vehicleRepository.existsByVehicleNumberIgnoreCase(normalizedNumber)) {
            throw new DuplicateResourceException("Vehicle", "vehicleNumber", normalizedNumber);
        }

        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(normalizedNumber)
                .company(request.getCompany().trim())
                .type(request.getType())
                .brand(request.getBrand().trim())
                .model(request.getModel().trim())
                .status(request.getStatus())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Created vehicle with id: {} and number: {}", saved.getId(), saved.getVehicleNumber());
        return VehicleResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(Long id, VehicleUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

        String normalizedNumber = request.getVehicleNumber().trim().toUpperCase();

        if (vehicleRepository.existsByVehicleNumberIgnoreCaseAndIdNot(normalizedNumber, id)) {
            throw new DuplicateResourceException("Vehicle", "vehicleNumber", normalizedNumber);
        }

        vehicle.setVehicleNumber(normalizedNumber);
        vehicle.setCompany(request.getCompany().trim());
        vehicle.setType(request.getType());
        vehicle.setBrand(request.getBrand().trim());
        vehicle.setModel(request.getModel().trim());
        vehicle.setStatus(request.getStatus());

        Vehicle updated = vehicleRepository.save(vehicle);
        log.info("Updated vehicle with id: {} and number: {}", updated.getId(), updated.getVehicleNumber());
        return VehicleResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

        vehicleRepository.delete(vehicle);
        log.info("Deleted vehicle with id: {} and number: {}", id, vehicle.getVehicleNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleStatsResponse getFleetStats() {
        long total = vehicleRepository.count();
        long active = vehicleRepository.countByStatus(VehicleStatus.ACTIVE);
        long maintenance = vehicleRepository.countByStatus(VehicleStatus.MAINTENANCE);
        long inactive = vehicleRepository.countByStatus(VehicleStatus.INACTIVE);

        return VehicleStatsResponse.builder()
                .totalCount(total)
                .activeCount(active)
                .maintenanceCount(maintenance)
                .inactiveCount(inactive)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getCompanies() {
        return vehicleRepository.findDistinctCompanies();
    }
}
