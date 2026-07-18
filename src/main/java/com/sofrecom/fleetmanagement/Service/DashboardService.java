package com.sofrecom.fleetmanagement.Service;

import com.sofrecom.fleetmanagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DashboardService {

    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private AlertRepository   alertRepository;
    @Autowired private TrajetRepository  trajetRepository;
    @Autowired private EventRepository   eventRepository;
    @Autowired private RedisTemplate<String, String> redisTemplate;

    public Map<String, Object> getKPIs() {
        Map<String, Object> kpis = new HashMap<>();

        // Véhicules
        long totalVehicles  = vehicleRepository.count();
        long actifs         = vehicleRepository.findByStatut("ACTIF").size();
        long arretes        = vehicleRepository.findByStatut("ARRETE").size();
        long maintenance    = vehicleRepository.findByStatut("MAINTENANCE").size();

        // Live — véhicules en mouvement (dans Redis)
        Set<String> liveKeys    = redisTemplate.keys("vehicle:live:*");
        long enMouvement        = liveKeys != null ? liveKeys.size() : 0;

        // Alertes
        long totalAlerts        = alertRepository.count();
        long speedAlerts        = alertRepository.findByType("SPEED").size();
        long tempAlerts         = alertRepository.findByType("TEMPERATURE").size();
        long geofenceAlerts     = alertRepository.findByType("GEOFENCE").size();

        // Trajets
        long totalTrajets       = trajetRepository.count();

        // Events
        long totalAccidents     = eventRepository.findByVehicleIdAndType(0L, "ACCIDENT").size();

        kpis.put("totalVehicles",   totalVehicles);
        kpis.put("actifs",          actifs);
        kpis.put("arretes",         arretes);
        kpis.put("maintenance",     maintenance);
        kpis.put("enMouvement",     enMouvement);
        kpis.put("totalAlerts",     totalAlerts);
        kpis.put("speedAlerts",     speedAlerts);
        kpis.put("tempAlerts",      tempAlerts);
        kpis.put("geofenceAlerts",  geofenceAlerts);
        kpis.put("totalTrajets",    totalTrajets);

        return kpis;
    }
}