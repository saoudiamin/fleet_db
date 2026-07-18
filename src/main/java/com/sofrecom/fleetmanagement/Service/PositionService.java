package com.sofrecom.fleetmanagement.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.fleetmanagement.Repository.AlertRepository;
import com.sofrecom.fleetmanagement.Repository.PositionRepository;
import com.sofrecom.fleetmanagement.Repository.VehicleRepository;
import com.sofrecom.fleetmanagement.model.Alert;
import com.sofrecom.fleetmanagement.model.Position;
import com.sofrecom.fleetmanagement.model.Vehicle;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sofrecom.fleetmanagement.Repository.GeofenceRepository;
import com.sofrecom.fleetmanagement.model.Geofence;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PositionService {

    private static final double SPEED_LIMIT = 120.0;
    private static final double TEMP_LIMIT = 90.0;

    private final PositionRepository positionRepository;
    private final VehicleRepository vehicleRepository;
    private final AlertRepository alertRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeofenceRepository geofenceRepository;

    public PositionService(PositionRepository positionRepository,
                           VehicleRepository vehicleRepository,
                           AlertRepository alertRepository,
                           RedisTemplate<String, String> redisTemplate, GeofenceRepository geofenceRepository) {
        this.positionRepository = positionRepository;
        this.vehicleRepository = vehicleRepository;
        this.alertRepository = alertRepository;
        this.redisTemplate = redisTemplate;
        this.geofenceRepository = geofenceRepository;
    }

    @Transactional
    public void savePosition(Long vehicleId, Double lat, Double lng,
                             Double speed, Double temperature) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + vehicleId));

        LocalDateTime timestamp = LocalDateTime.now();

        Position position = new Position();
        position.setVehicle(vehicle);
        position.setLat(lat);
        position.setLng(lng);
        position.setSpeed(speed);
        position.setTemperature(temperature);
        position.setTimestamp(timestamp);
        positionRepository.save(position);

        updateLivePosition(vehicleId, lat, lng, speed, temperature, timestamp);
        checkAlerts(vehicle, lat, lng, speed, temperature, timestamp); // ← زيد lat, lng
    }

    private void checkAlerts(Vehicle vehicle, Double lat, Double lng,
                             Double speed, Double temperature,
                             LocalDateTime timestamp) {

        // Speed
        if (speed != null && speed > SPEED_LIMIT) {
            saveAlert(vehicle, "SPEED", speed, timestamp);
            System.out.println("⚠️ SPEED ALERT - Vehicle " + vehicle.getId() + " : " + speed + " km/h");
        }

        // Temperature
        if (temperature != null && temperature > TEMP_LIMIT) {
            saveAlert(vehicle, "TEMPERATURE", temperature, timestamp);
            System.out.println("⚠️ TEMP ALERT - Vehicle " + vehicle.getId() + " : " + temperature + "°C");
        }

        // Geofence
        if (lat != null && lng != null) {
            geofenceRepository.findAll().forEach(geo -> {
                double distance = calculateDistance(lat, lng,
                        geo.getLatCenter(), geo.getLngCenter());
                if (distance > geo.getRadiusKm()) {
                    saveAlert(vehicle, "GEOFENCE", distance, timestamp);
                    System.out.println("📍 GEOFENCE ALERT - Vehicle "
                            + vehicle.getId() + " outside " + geo.getNom());
                }
            });
        }
    }

    private double calculateDistance(double lat1, double lng1,
                                     double lat2, double lng2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    public String getLivePosition(Long vehicleId) {
        try {
            return redisTemplate.opsForValue().get(livePositionKey(vehicleId));
        } catch (Exception e) {
            System.err.println("Redis error: " + e.getMessage());
            return null;
        }
    }

    public Map<String, String> getAllLivePositions() {
        Map<String, String> positions = new HashMap<>();

        try {
            var keys = redisTemplate.keys("vehicle:live:*");
            if (keys != null) {
                for (String key : keys) {
                    positions.put(key, redisTemplate.opsForValue().get(key));
                }
            }
        } catch (Exception e) {
            System.err.println("Redis error: " + e.getMessage());
        }

        return positions;
    }

    public Optional<Position> getLastPosition(Long vehicleId) {
        return positionRepository.findTopByVehicleIdOrderByTimestampDesc(vehicleId);
    }

    public List<Position> getPositionHistory(Long vehicleId, LocalDateTime start, LocalDateTime end) {
        return positionRepository.findByVehicleIdAndTimestampBetween(vehicleId, start, end);
    }

    private void updateLivePosition(Long vehicleId, Double lat, Double lng,
                                    Double speed, Double temperature,
                                    LocalDateTime timestamp) {
        try {
            Map<String, Object> liveData = new HashMap<>();
            liveData.put("vehicleId", vehicleId);
            liveData.put("lat", lat);
            liveData.put("lng", lng);
            liveData.put("speed", speed);
            liveData.put("temperature", temperature);
            liveData.put("timestamp", timestamp.toString());

            redisTemplate.opsForValue().set(
                    livePositionKey(vehicleId),
                    objectMapper.writeValueAsString(liveData)
            );
        } catch (Exception e) {
            System.err.println("Redis error: " + e.getMessage());
        }
    }

    private void checkAlerts(Vehicle vehicle, Double speed, Double temperature, LocalDateTime timestamp) {
        if (speed != null && speed > SPEED_LIMIT) {
            saveAlert(vehicle, "SPEED", speed, timestamp);
            System.out.println("SPEED ALERT - Vehicle " + vehicle.getId() + " : " + speed + " km/h");
        }

        if (temperature != null && temperature > TEMP_LIMIT) {
            saveAlert(vehicle, "TEMPERATURE", temperature, timestamp);
            System.out.println("TEMP ALERT - Vehicle " + vehicle.getId() + " : " + temperature + " C");
        }
    }

    private void saveAlert(Vehicle vehicle, String type, Double value, LocalDateTime timestamp) {
        Alert alert = new Alert();
        alert.setVehicle(vehicle);
        alert.setType(type);
        alert.setValeur(value);
        alert.setTimestamp(timestamp);
        alertRepository.save(alert);
    }

    private String livePositionKey(Long vehicleId) {
        return "vehicle:live:" + vehicleId;
    }
}
