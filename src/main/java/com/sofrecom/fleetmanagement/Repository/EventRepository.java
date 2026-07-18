package com.sofrecom.fleetmanagement.Repository;

import com.sofrecom.fleetmanagement.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByVehicleId(Long vehicleId);
    List<Event> findByVehicleIdAndType(Long vehicleId, String type);
    Long countByVehicleIdAndType(Long vehicleId, String type);
}