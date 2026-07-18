package com.sofrecom.fleetmanagement.model;

import com.sofrecom.fleetmanagement.model.Vehicle;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private Double lat;
    private Double lng;
    private Double speed;
    private Double temperature;
    private LocalDateTime timestamp;
}