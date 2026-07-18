package com.sofrecom.fleetmanagement.Repository;

import com.sofrecom.fleetmanagement.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByVehicleId(Long vehicleId);
    List<Alert> findByType(String type);
    List<Alert> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    Long countByVehicleId(Long vehicleId);
}