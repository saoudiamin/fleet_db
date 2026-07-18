package com.sofrecom.fleetmanagement.model;

import com.sofrecom.fleetmanagement.model.Vehicle;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private String type; // FREINAGE / ACCELERATION / ACCIDENT
    private Double valeur;
    private Double lat;
    private Double lng;
    private LocalDateTime timestamp;
}