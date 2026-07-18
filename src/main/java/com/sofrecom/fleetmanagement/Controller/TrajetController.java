package com.sofrecom.fleetmanagement.Controller;

import com.sofrecom.fleetmanagement.model.Trajet;
import com.sofrecom.fleetmanagement.Service.TrajetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trajets")
@CrossOrigin(origins = "*")
public class TrajetController {

    @Autowired
    private TrajetService trajetService;

    // GET tous les trajets
    @GetMapping
    public List<Trajet> getAllTrajets() {
        return trajetService.getAllTrajets();
    }

    // GET trajets par véhicule
    @GetMapping("/vehicle/{vehicleId}")
    public List<Trajet> getByVehicle(@PathVariable Long vehicleId) {
        return trajetService.getByVehicleId(vehicleId);
    }

    // GET trajets par véhicule et période
    @GetMapping("/vehicle/{vehicleId}/period")
    public List<Trajet> getByPeriod(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return trajetService.getByVehicleAndPeriod(vehicleId, start, end);
    }

    // GET distance totale par véhicule
    @GetMapping("/vehicle/{vehicleId}/distance")
    public ResponseEntity<Double> getTotalDistance(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(trajetService.getTotalDistance(vehicleId));
    }

    // POST démarrer un trajet
    @PostMapping("/start/{vehicleId}")
    public ResponseEntity<Trajet> startTrajet(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(trajetService.startTrajet(vehicleId));
    }

    // PUT terminer un trajet
    @PutMapping("/end/{trajetId}")
    public ResponseEntity<Trajet> endTrajet(
            @PathVariable Long trajetId,
            @RequestBody Map<String, Double> body) {
        Double distance    = body.get("distanceKm");
        Double consommation = body.get("consommationLitres");
        return ResponseEntity.ok(trajetService.endTrajet(trajetId, distance, consommation));
    }

    // DELETE trajet
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrajet(@PathVariable Long id) {
        trajetService.deleteTrajet(id);
        return ResponseEntity.noContent().build();
    }
}