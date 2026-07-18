package com.sofrecom.fleetmanagement.model;

import com.sofrecom.fleetmanagement.model.Vehicle;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chauffeurs")
public class Chauffeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String telephone;
    private String permis;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
}