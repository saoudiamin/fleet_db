package com.sofrecom.fleetmanagement.Service;

import com.sofrecom.fleetmanagement.model.Chauffeur;
import com.sofrecom.fleetmanagement.Repository.ChauffeurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChauffeurService {

    @Autowired
    private ChauffeurRepository chauffeurRepository;

    public List<Chauffeur> getAllChauffeurs() {
        return chauffeurRepository.findAll();
    }

    public Chauffeur getChauffeurById(Long id) {
        return chauffeurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chauffeur not found: " + id));
    }

    public Chauffeur createChauffeur(Chauffeur chauffeur) {
        return chauffeurRepository.save(chauffeur);
    }

    public Chauffeur updateChauffeur(Long id, Chauffeur updated) {
        Chauffeur chauffeur = getChauffeurById(id);
        chauffeur.setNom(updated.getNom());
        chauffeur.setPrenom(updated.getPrenom());
        chauffeur.setTelephone(updated.getTelephone());
        chauffeur.setPermis(updated.getPermis());
        chauffeur.setVehicle(updated.getVehicle());
        return chauffeurRepository.save(chauffeur);
    }

    public void deleteChauffeur(Long id) {
        chauffeurRepository.deleteById(id);
    }

    public List<Chauffeur> getByVehicleId(Long vehicleId) {
        return chauffeurRepository.findByVehicleId(vehicleId);
    }
}