package com.sofrecom.fleetmanagement.Repository;

import com.sofrecom.fleetmanagement.model.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeofenceRepository extends JpaRepository<Geofence, Long> {
}