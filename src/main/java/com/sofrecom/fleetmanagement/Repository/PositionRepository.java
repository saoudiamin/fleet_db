package com.sofrecom.fleetmanagement.Repository;

import com.sofrecom.fleetmanagement.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {

    // آخر position لـ véhicule معين
    Optional<Position> findTopByVehicleIdOrderByTimestampDesc(Long vehicleId);

    // كل positions بين تاريخين
    List<Position> findByVehicleIdAndTimestampBetween(
            Long vehicleId,
            LocalDateTime start,
            LocalDateTime end
    );
}