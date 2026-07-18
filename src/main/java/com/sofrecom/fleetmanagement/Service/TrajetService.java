package com.sofrecom.fleetmanagement.Service;

import com.sofrecom.fleetmanagement.model.Trajet;
import com.sofrecom.fleetmanagement.model.Vehicle;
import com.sofrecom.fleetmanagement.Repository.TrajetRepository;
import com.sofrecom.fleetmanagement.Repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrajetService {

    @Autowired
    private TrajetRepository trajetRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Trajet> getAllTrajets() {
        return trajetRepository.findAll();
    }

    public List<Trajet> getByVehicleId(Long vehicleId) {
        return trajetRepository.findByVehicleId(vehicleId);
    }

    public List<Trajet> getByVehicleAndPeriod(Long vehicleId,
                                              LocalDateTime start,
                                              LocalDateTime end) {
        return trajetRepository.findByVehicleIdAndStartTimeBetween(vehicleId, start, end);
    }

    public Double getTotalDistance(Long vehicleId) {
        Double total = trajetRepository.sumDistanceByVehicleId(vehicleId);
        return total != null ? total : 0.0;
    }

    public Trajet createTrajet(Trajet trajet) {
        return trajetRepository.save(trajet);
    }

    public Trajet startTrajet(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + vehicleId));

        Trajet trajet = new Trajet();
        trajet.setVehicle(vehicle);
        trajet.setStartTime(LocalDateTime.now());
        trajet.setDistanceKm(0.0);
        trajet.setConsommationLitres(0.0);
        return trajetRepository.save(trajet);
    }

    public Trajet endTrajet(Long trajetId, Double distanceKm, Double consommation) {
        Trajet trajet = trajetRepository.findById(trajetId)
                .orElseThrow(() -> new RuntimeException("Trajet not found: " + trajetId));

        trajet.setEndTime(LocalDateTime.now());
        trajet.setDistanceKm(distanceKm);
        trajet.setConsommationLitres(consommation);
        return trajetRepository.save(trajet);
    }

    public void deleteTrajet(Long id) {
        trajetRepository.deleteById(id);
    }
}