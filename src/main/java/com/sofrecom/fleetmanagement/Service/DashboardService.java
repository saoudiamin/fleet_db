package com.sofrecom.fleetmanagement.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.fleetmanagement.Repository.*;
import com.sofrecom.fleetmanagement.model.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        long actifs = vehicleRepository.findByStatut("ACTIF").size();
        long arretes = vehicleRepository.findByStatut("ARRETE").size();
        long maintenance = vehicleRepository.findByStatut("MAINTENANCE").size();

        // Live — véhicules en mouvement (dans Redis)
        long enMouvement = countLiveVehicles();

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
            if (activeVehicles == null || activeVehicles.isEmpty()) {
                return 0;
            }

            ObjectMapper mapper = new ObjectMapper();
            LocalDateTime cutoff = LocalDateTime.now().minusSeconds(45);

            return activeVehicles.stream()
                    .map(Vehicle::getId)
                    .filter(Objects::nonNull)
                    .filter(id -> hasRecentPosition(id, mapper, cutoff))
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    private Long extractVehicleId(String key) {
        try {
            String suffix = key.substring(key.lastIndexOf(':') + 1);
            return Long.parseLong(suffix);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasRecentPosition(Long vehicleId, ObjectMapper mapper, LocalDateTime cutoff) {
        try {
            String payload = redisTemplate.opsForValue().get("vehicle:live:" + vehicleId);
            if (payload == null || payload.isBlank()) {
                return false;
            }

            Map<String, Object> data = mapper.readValue(payload, Map.class);
            Object timestamp = data.get("timestamp");
            if (timestamp == null) {
                return false;
            }

            LocalDateTime positionTime = LocalDateTime.parse(timestamp.toString());
            return !positionTime.isBefore(cutoff);
        } catch (Exception e) {
            return false;
        }
    }
}