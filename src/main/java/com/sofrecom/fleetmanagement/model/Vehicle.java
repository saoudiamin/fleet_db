package com.sofrecom.fleetmanagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imei;
    private String nom;
    private String type;
    private String matricule;
    private String modele;
    private String statut; // ACTIF / ARRETE / MAINTENANCE

    @ManyToOne
    @JoinColumn(name = "client_id")
    private com.sofrecom.fleetmanagement.model.Client client;
}