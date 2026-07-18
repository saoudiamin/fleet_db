package com.sofrecom.fleetmanagement.Repository;

import com.sofrecom.fleetmanagement.model.Trajet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {

    List<Trajet> findByVehicleId(Long vehicleId);

    // مجموع المسافة لـ véhicule معين
    @Query("SELECT SUM(t.distanceKm) FROM Trajet t WHERE t.vehicle.id = :vehicleId")
    Double sumDistanceByVehicleId(Long vehicleId);

    // trajets بين تاريخين
    List<Trajet> findByVehicleIdAndStartTimeBetween(
            Long vehicleId,
            LocalDateTime start,
            LocalDateTime end
    );
}