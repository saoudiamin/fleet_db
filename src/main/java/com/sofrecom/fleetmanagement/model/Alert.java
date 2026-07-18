package com.sofrecom.fleetmanagement.model;

import com.sofrecom.fleetmanagement.model.Vehicle;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private String type; // SPEED / TEMPERATURE / GEOFENCE / ARRET / BATTERIE
    private Double valeur;
    private LocalDateTime timestamp;
}