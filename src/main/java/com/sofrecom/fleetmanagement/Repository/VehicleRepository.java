package com.sofrecom.fleetmanagement.Repository;

import com.sofrecom.fleetmanagement.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByStatut(String statut);
    List<Vehicle> findByClientId(Long clientId);
}