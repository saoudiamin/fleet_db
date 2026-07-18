package com.sofrecom.fleetmanagement.Service;

import com.sofrecom.fleetmanagement.model.Vehicle;
import com.sofrecom.fleetmanagement.Repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + id));
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long id, Vehicle updated) {
        Vehicle vehicle = getVehicleById(id);
        vehicle.setNom(updated.getNom());
        vehicle.setType(updated.getType());
        vehicle.setMatricule(updated.getMatricule());
        vehicle.setModele(updated.getModele());
        vehicle.setStatut(updated.getStatut());
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    public List<Vehicle> getByStatut(String statut) {
        return vehicleRepository.findByStatut(statut);
    }
}