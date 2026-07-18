package com.sofrecom.fleetmanagement.Controller;

import com.sofrecom.fleetmanagement.Service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/positions")
@CrossOrigin(origins = "*")
public class PositionController {

    @Autowired
    private PositionService positionService;

    // POST reçoit position GPS
    @PostMapping
    public ResponseEntity<String> receivePosition(@RequestBody Map<String, Object> body) {
        Long vehicleId  = Long.valueOf(body.get("vehicleId").toString());
        Double lat      = Double.valueOf(body.get("lat").toString());
        Double lng      = Double.valueOf(body.get("lng").toString());
        Double speed    = Double.valueOf(body.get("speed").toString());
        Double temp     = body.get("temperature") != null ?
                Double.valueOf(body.get("temperature").toString()) : null;

        positionService.savePosition(vehicleId, lat, lng, speed, temp);
        return ResponseEntity.ok("Position saved");
    }

    // GET position live d'un véhicule (depuis Redis)
    @GetMapping("/live/{vehicleId}")
    public ResponseEntity<String> getLivePosition(@PathVariable Long vehicleId) {
        String position = positionService.getLivePosition(vehicleId);
        if (position == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(position);
    }

    // GET toutes les positions live (pour la map)
    @GetMapping("/live")
    public ResponseEntity<Map<String, String>> getAllLive() {
        return ResponseEntity.ok(positionService.getAllLivePositions());
    }
}