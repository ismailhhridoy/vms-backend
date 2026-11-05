package com.starkgroup.vms.common.config;

import com.starkgroup.vms.vehicle.entity.Vehicle;
import com.starkgroup.vms.vehicle.entity.VehicleStatus;
import com.starkgroup.vms.vehicle.entity.VehicleType;
import com.starkgroup.vms.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final VehicleRepository vehicleRepository;

    @Override
    public void run(String... args) {
        if (vehicleRepository.count() == 0) {
            log.info("Seeding initial Stark Group fleet data...");

            List<Vehicle> initialVehicles = List.of(
                    Vehicle.builder()
                            .vehicleNumber("SG-TRK-1042")
                            .company("Stark Logistics")
                            .type(VehicleType.TRUCK)
                            .brand("Ford")
                            .model("F-150 Lightning")
                            .status(VehicleStatus.ACTIVE)
                            .build(),
                    Vehicle.builder()
                            .vehicleNumber("SG-SED-2018")
                            .company("Stark Industries")
                            .type(VehicleType.SEDAN)
                            .brand("Toyota")
                            .model("Camry Hybrid")
                            .status(VehicleStatus.ACTIVE)
                            .build(),
                    Vehicle.builder()
                            .vehicleNumber("SG-EV-3091")
                            .company("Stark Clean Energy")
                            .type(VehicleType.ELECTRIC)
                            .brand("Tesla")
                            .model("Model Y")
                            .status(VehicleStatus.ACTIVE)
                            .build(),
                    Vehicle.builder()
                            .vehicleNumber("SG-VAN-4005")
                            .company("Stark Logistics")
                            .type(VehicleType.VAN)
                            .brand("Mercedes-Benz")
                            .model("Sprinter 2500")
                            .status(VehicleStatus.MAINTENANCE)
                            .build(),
                    Vehicle.builder()
                            .vehicleNumber("SG-SUV-5120")
                            .company("Stark Security")
                            .type(VehicleType.SUV)
                            .brand("Chevrolet")
                            .model("Tahoe Premier")
                            .status(VehicleStatus.ACTIVE)
                            .build(),
                    Vehicle.builder()
                            .vehicleNumber("SG-TRK-1089")
                            .company("Stark Aerospace")
                            .type(VehicleType.TRUCK)
                            .brand("Volvo")
                            .model("VNL 860")
                            .status(VehicleStatus.INACTIVE)
                            .build(),
                    Vehicle.builder()
                            .vehicleNumber("SG-SED-2044")
                            .company("Stark Industries")
                            .type(VehicleType.SEDAN)
                            .brand("Honda")
                            .model("Accord Touring")
                            .status(VehicleStatus.ACTIVE)
                            .build(),
                    Vehicle.builder()
                            .vehicleNumber("SG-EV-3012")
                            .company("Stark Clean Energy")
                            .type(VehicleType.ELECTRIC)
                            .brand("Rivian")
                            .model("R1T Adventure")
                            .status(VehicleStatus.MAINTENANCE)
                            .build()
            );

            vehicleRepository.saveAll(initialVehicles);
            log.info("Successfully seeded {} fleet vehicles.", initialVehicles.size());
        }
    }
}

