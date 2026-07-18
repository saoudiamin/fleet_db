package com.sofrecom.fleetmanagement.Controller;

import com.sofrecom.fleetmanagement.model.Alert;
import com.sofrecom.fleetmanagement.Repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    // GET toutes les alertes
    @GetMapping
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    // GET alertes par véhicule
    @GetMapping("/vehicle/{vehicleId}")
    public List<Alert> getByVehicle(@PathVariable Long vehicleId) {
        return alertRepository.findByVehicleId(vehicleId);
    }

    // GET alertes par type
    @GetMapping("/type/{type}")
    public List<Alert> getByType(@PathVariable String type) {
        return alertRepository.findByType(type);
    }
}