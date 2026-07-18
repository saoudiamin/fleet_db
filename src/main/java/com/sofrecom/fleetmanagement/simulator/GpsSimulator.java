package com.sofrecom.fleetmanagement.simulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.fleetmanagement.Repository.VehicleRepository;
import com.sofrecom.fleetmanagement.model.Vehicle;
import com.sofrecom.fleetmanagement.Service.PositionService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class GpsSimulator {

    @Autowired private PositionService positionService;
    @Autowired private VehicleRepository vehicleRepository;

    private final Random random     = new Random();
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http   = HttpClient.newHttpClient();

    // Per vehicle state
    private final Map<Long, List<double[]>> vehicleRoutes = new HashMap<>();
    private final Map<Long, Integer>        positionIndex = new HashMap<>();
    private final Map<Long, Double>         speedMap      = new HashMap<>();

    // Real Tunis road endpoints [startLng, startLat, endLng, endLat]
    private static final double[][] ROUTE_ENDPOINTS = {
            {10.1815, 36.8192, 10.1330, 36.8065},  // Bourguiba → Bardo
            {10.1660, 36.8190, 10.3248, 36.8509},  // Centre → Carthage
            {10.1653, 36.8192, 10.1840, 36.8730},  // Tunis → Ariana
            {10.1653, 36.8192, 10.2100, 36.7560},  // Tunis → Ben Arous
            {10.1653, 36.8192, 10.0670, 36.7750},  // Tunis → Manouba
            {10.1653, 36.8192, 10.2976, 36.7364},  // Tunis → Hammam Lif
            {10.1653, 36.8192, 10.1176, 36.9164},  // Tunis → Bizerte dir
            {10.2089, 36.8383, 10.1738, 36.8266},  // Berges du Lac
            {10.1653, 36.8192, 10.2976, 36.6914},  // Tunis → Nabeul dir
            {10.1815, 36.8065, 10.1653, 36.8192},  // Bardo → Bourguiba (return)
    };

    @PostConstruct
    public void init() {
        System.out.println("🗺️ GpsSimulator: fetching real road routes from OSRM...");
        List<Vehicle> vehicles = vehicleRepository.findAll();
        for (int i = 0; i < Math.min(vehicles.size(), ROUTE_ENDPOINTS.length); i++) {
            Vehicle v = vehicles.get(i);
            getOrFetchRoute(v.getId(), i);
            try { Thread.sleep(1100); } catch (InterruptedException ignored) {}
        }
        System.out.println("🚀 Routes ready — simulator starting");
    }

    /**
     * Calls OSRM API and returns list of [lat, lng] points along real roads
     */
    private List<double[]> fetchRoute(double startLng, double startLat,
                                      double endLng,   double endLat) {
        try {
            String url = String.format(
                    "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                    startLng, startLat, endLng, endLat
            );

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET().build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(res.body());
            JsonNode coords = root.path("routes").get(0)
                    .path("geometry")
                    .path("coordinates");

            List<double[]> points = new ArrayList<>();
            for (JsonNode point : coords) {
                double lng = point.get(0).asDouble();
                double lat = point.get(1).asDouble();
                points.add(new double[]{lat, lng});
            }
            return points;

        } catch (Exception e) {
            System.err.println("OSRM error: " + e.getMessage());
            return null;
        }
    }

    private List<double[]> getOrFetchRoute(Long vehicleId, int routeNum) {
        if (!vehicleRoutes.containsKey(vehicleId)) {
            double[] ep = ROUTE_ENDPOINTS[routeNum % ROUTE_ENDPOINTS.length];
            List<double[]> route = fetchRoute(ep[0], ep[1], ep[2], ep[3]);

            // Fallback if OSRM fails
            if (route == null || route.isEmpty()) {
                route = buildFallback(routeNum);
            }
            vehicleRoutes.put(vehicleId, route);
            positionIndex.put(vehicleId, 0);
        }
        return vehicleRoutes.get(vehicleId);
    }

    // Simple fallback if OSRM is unreachable
    private List<double[]> buildFallback(int routeNum) {
        double[] ep = ROUTE_ENDPOINTS[routeNum % ROUTE_ENDPOINTS.length];
        List<double[]> pts = new ArrayList<>();
        int steps = 30;
        for (int i = 0; i <= steps; i++) {
            double t   = (double) i / steps;
            double lat = ep[1] + t * (ep[3] - ep[1]);
            double lng = ep[0] + t * (ep[2] - ep[0]);
            pts.add(new double[]{lat, lng});
        }
        return pts;
    }

    @Scheduled(fixedRate = 2000) // every 2s
    public void simulateGPS() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        if (vehicles.isEmpty()) return;

        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            Long vehicleId  = vehicle.getId();

            List<double[]> route = getOrFetchRoute(vehicleId, i);
            int idx = positionIndex.getOrDefault(vehicleId, 0);

            double[] point = route.get(idx);
            double lat = point[0] + (random.nextDouble() - 0.5) * 0.00003; // ~3m noise
            double lng = point[1] + (random.nextDouble() - 0.5) * 0.00003;

            // Advance — loop back at end
            positionIndex.put(vehicleId, (idx + 1) % route.size());

            // Smooth speed change
            double prevSpeed = speedMap.getOrDefault(vehicleId, 50.0);
            double delta     = (random.nextDouble() - 0.5) * 8;
            double speed     = Math.max(20, Math.min(100, prevSpeed + delta));
            if (random.nextDouble() < 0.04) speed = 125 + random.nextDouble() * 20;
            speedMap.put(vehicleId, speed);

            double temp = 65 + random.nextDouble() * 20;
            if (random.nextDouble() < 0.04) temp = 92 + random.nextDouble() * 10;

            positionService.savePosition(vehicleId,
                    Math.round(lat * 1e6) / 1e6,
                    Math.round(lng * 1e6) / 1e6,
                    Math.round(speed * 10.0) / 10.0,
                    Math.round(temp  * 10.0) / 10.0);
        }
    }
}