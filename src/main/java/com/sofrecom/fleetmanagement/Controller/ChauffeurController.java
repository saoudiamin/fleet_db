package com.sofrecom.fleetmanagement.Controller;

import com.sofrecom.fleetmanagement.model.Chauffeur;
import com.sofrecom.fleetmanagement.Service.ChauffeurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chauffeurs")
@CrossOrigin(origins = "*")
public class ChauffeurController {

    @Autowired
    private ChauffeurService chauffeurService;

    // GET tous les chauffeurs
    @GetMapping
    public List<Chauffeur> getAllChauffeurs() {
        return chauffeurService.getAllChauffeurs();
    }

    // GET chauffeur par ID
    @GetMapping("/{id}")
    public ResponseEntity<Chauffeur> getChauffeurById(@PathVariable Long id) {
        return ResponseEntity.ok(chauffeurService.getChauffeurById(id));
    }

    // GET chauffeur par véhicule
    @GetMapping("/vehicle/{vehicleId}")
    public List<Chauffeur> getByVehicle(@PathVariable Long vehicleId) {
        return chauffeurService.getByVehicleId(vehicleId);
    }

    // POST créer chauffeur
    @PostMapping
    public ResponseEntity<Chauffeur> createChauffeur(@RequestBody Chauffeur chauffeur) {
        return ResponseEntity.ok(chauffeurService.createChauffeur(chauffeur));
    }

    // PUT modifier chauffeur
    @PutMapping("/{id}")
    public ResponseEntity<Chauffeur> updateChauffeur(@PathVariable Long id,
                                                     @RequestBody Chauffeur chauffeur) {
        return ResponseEntity.ok(chauffeurService.updateChauffeur(id, chauffeur));
    }

    // DELETE supprimer chauffeur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChauffeur(@PathVariable Long id) {
        chauffeurService.deleteChauffeur(id);
        return ResponseEntity.noContent().build();
    }
}