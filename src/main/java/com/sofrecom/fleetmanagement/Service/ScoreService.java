package com.sofrecom.fleetmanagement.Service;

import com.sofrecom.fleetmanagement.Repository.AlertRepository;
import com.sofrecom.fleetmanagement.Repository.EventRepository;
import com.sofrecom.fleetmanagement.Repository.TrajetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ScoreService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TrajetRepository trajetRepository;

    /**
     * Score Chauffeur /100
     * Sécurité      40% → excès vitesse
     * Consommation  30% → litres/km
     * Freinage      20% → freinages brusques
     * Discipline    10% → alertes geofence
     */
    public Map<String, Object> getChauffeurScore(Long vehicleId) {
        // Excès vitesse
        long speedAlerts    = alertRepository.countByVehicleId(vehicleId);
        long freinages      = eventRepository.countByVehicleIdAndType(vehicleId, "FREINAGE");
        long geofenceAlerts = alertRepository.findByType("GEOFENCE")
                .stream()
                .filter(a -> a.getVehicle() != null && vehicleId.equals(a.getVehicle().getId()))
                .count();

        // Distance totale
        Double totalDistance = trajetRepository.sumDistanceByVehicleId(vehicleId);
        if (totalDistance == null) totalDistance = 1.0;

        // Calcul scores par critère
        double securite    = Math.max(0, 100 - (speedAlerts * 5));
        double freinage    = Math.max(0, 100 - (freinages * 3));
        double discipline  = Math.max(0, 100 - (geofenceAlerts * 10));
        double consommation = 80.0; // valeur par défaut

        // Score final pondéré
        double score = (securite * 0.40)
                + (consommation * 0.30)
                + (freinage * 0.20)
                + (discipline * 0.10);

        Map<String, Object> result = new HashMap<>();
        result.put("vehicleId",    vehicleId);
        result.put("scoreGlobal",  Math.round(score));
        result.put("securite",     Math.round(securite));
        result.put("freinage",     Math.round(freinage));
        result.put("discipline",   Math.round(discipline));
        result.put("consommation", Math.round(consommation));
        result.put("speedAlerts",  speedAlerts);
        result.put("freinages",    freinages);
        return result;
    }

    /**
     * Score Véhicule /100
     * Consommation  40%
     * Utilisation   30%
     * Sécurité      20%
     * Maintenance   10%
     */
    public Map<String, Object> getVehicleScore(Long vehicleId) {
        Double totalDistance = trajetRepository.sumDistanceByVehicleId(vehicleId);
        if (totalDistance == null) totalDistance = 0.0;

        long totalAlerts = alertRepository.countByVehicleId(vehicleId);

        double utilisation  = Math.min(100, totalDistance / 10);
        double securite     = Math.max(0, 100 - (totalAlerts * 3));
        double consommation = 75.0;
        double maintenance  = 90.0;

        double score = (consommation * 0.40)
                + (utilisation  * 0.30)
                + (securite     * 0.20)
                + (maintenance  * 0.10);

        Map<String, Object> result = new HashMap<>();
        result.put("vehicleId",    vehicleId);
        result.put("scoreGlobal",  Math.round(score));
        result.put("consommation", Math.round(consommation));
        result.put("utilisation",  Math.round(utilisation));
        result.put("securite",     Math.round(securite));
        result.put("maintenance",  Math.round(maintenance));
        result.put("totalAlerts",  totalAlerts);
        result.put("totalDistance", totalDistance);
        return result;
    }
}
