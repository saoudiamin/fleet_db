package com.sofrecom.fleetmanagement.Repository;

import com.sofrecom.fleetmanagement.model.Chauffeur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChauffeurRepository extends JpaRepository<Chauffeur, Long> {
    List<Chauffeur> findByVehicleId(Long vehicleId);
}