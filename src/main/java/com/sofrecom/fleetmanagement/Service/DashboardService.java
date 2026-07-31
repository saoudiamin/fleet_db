package com.sofrecom.fleetmanagement.Service;

import com.sofrecom.fleetmanagement.Repository.*;
import com.sofrecom.fleetmanagement.model.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private TrajetRepository trajetRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public Map<String, Object> getKPIs() {
        Map<String, Object> kpis = new HashMap<>();

        // Véhicules
        long totalVehicles = vehicleRepository.count();
        long arretes = vehicleRepository.findByStatut("ARRETE").size();
        long maintenance = vehicleRepository.findByStatut("MAINTENANCE").size();

        // Same source as /api/positions/live: only DB vehicles marked ACTIF with live Redis data.
        long enMouvement = countLiveVehicles();
        long actifs = enMouvement;

        // Alertes
        long totalAlerts = alertRepository.count();
        long speedAlerts = alertRepository.findByType("SPEED").size();
        long tempAlerts = alertRepository.findByType("TEMPERATURE").size();
        long geofenceAlerts = alertRepository.findByType("GEOFENCE").size();

        // Trajets
        long totalTrajets = trajetRepository.count();

        // Events
        long totalAccidents = eventRepository.findByVehicleIdAndType(0L, "ACCIDENT").size();

        kpis.put("totalVehicles", totalVehicles);
        kpis.put("actifs", actifs);
        kpis.put("arretes", arretes);
        kpis.put("maintenance", maintenance);
        kpis.put("enMouvement", enMouvement);
        kpis.put("totalAlerts", totalAlerts);
        kpis.put("speedAlerts", speedAlerts);
        kpis.put("tempAlerts", tempAlerts);
        kpis.put("geofenceAlerts", geofenceAlerts);
        kpis.put("totalTrajets", totalTrajets);

        return kpis;
    }

    private long countLiveVehicles() {
        try {
            List<Vehicle> activeVehicles = vehicleRepository.findByStatut("ACTIF");
            return activeVehicles.stream()
                    .filter(vehicle -> vehicle.getId() != null)
                    .filter(vehicle -> redisTemplate.opsForValue().get("vehicle:live:" + vehicle.getId()) != null)
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }
}
