package com.sofrecom.fleetmanagement.model;

import com.sofrecom.fleetmanagement.model.Vehicle;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "maintenance")
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private String type;
    private LocalDateTime dateIntervention;
    private Double kilometrage;
    private Double prochainKm;
    private String notes;
}