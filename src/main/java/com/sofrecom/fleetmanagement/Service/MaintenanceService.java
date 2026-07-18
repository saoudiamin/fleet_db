package com.sofrecom.fleetmanagement.Service;

import com.sofrecom.fleetmanagement.model.Maintenance;
import com.sofrecom.fleetmanagement.model.Vehicle;
import com.sofrecom.fleetmanagement.Repository.MaintenanceRepository;
import com.sofrecom.fleetmanagement.Repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Maintenance> getAllMaintenances() {
        return maintenanceRepository.findAll();
    }

    public List<Maintenance> getByVehicleId(Long vehicleId) {
        return maintenanceRepository.findByVehicleId(vehicleId);
    }

    public Maintenance createMaintenance(Maintenance maintenance) {
        return maintenanceRepository.save(maintenance);
    }

    public Maintenance updateMaintenance(Long id, Maintenance updated) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance not found: " + id));
        maintenance.setType(updated.getType());
        maintenance.setDateIntervention(updated.getDateIntervention());
        maintenance.setKilometrage(updated.getKilometrage());
        maintenance.setProchainKm(updated.getProchainKm());
        maintenance.setNotes(updated.getNotes());
        return maintenanceRepository.save(maintenance);
    }

    public void deleteMaintenance(Long id) {
        maintenanceRepository.deleteById(id);
    }

    // Véhicules qui approchent de la maintenance
    public List<Maintenance> getUpcomingMaintenances(Double currentKm) {
        return maintenanceRepository.findAll().stream()
                .filter(m -> m.getProchainKm() != null &&
                        m.getProchainKm() - currentKm <= 500)
                .toList();
    }
}