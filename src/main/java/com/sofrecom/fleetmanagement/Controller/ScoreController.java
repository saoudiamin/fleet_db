package com.sofrecom.fleetmanagement.Controller;

import com.sofrecom.fleetmanagement.Service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    // GET score chauffeur par véhicule
    @GetMapping("/chauffeur/{vehicleId}")
    public ResponseEntity<Map<String, Object>> getChauffeurScore(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(scoreService.getChauffeurScore(vehicleId));
    }

    // GET score véhicule
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<Map<String, Object>> getVehicleScore(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(scoreService.getVehicleScore(vehicleId));
    }
}