package com.sofrecom.fleetmanagement.Service;

import com.sofrecom.fleetmanagement.model.Alert;
import com.sofrecom.fleetmanagement.Repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public List<Alert> getByVehicleId(Long vehicleId) {
        return alertRepository.findByVehicleId(vehicleId);
    }

    public List<Alert> getByType(String type) {
        return alertRepository.findByType(type);
    }

    public List<Alert> getByPeriod(LocalDateTime start, LocalDateTime end) {
        return alertRepository.findByTimestampBetween(start, end);
    }

    public Long countByVehicle(Long vehicleId) {
        return alertRepository.countByVehicleId(vehicleId);
    }

    public void deleteAlert(Long id) {
        alertRepository.deleteById(id);
    }
}