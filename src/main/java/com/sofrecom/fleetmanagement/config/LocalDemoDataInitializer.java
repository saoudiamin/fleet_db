package com.sofrecom.fleetmanagement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.fleetmanagement.Repository.*;
import com.sofrecom.fleetmanagement.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Profile("local")
public class LocalDemoDataInitializer {

    @Bean
    CommandLineRunner seedDemoData(
            ClientRepo clientRepo,
            VehicleRepository vehicleRepo,
            ChauffeurRepository chauffeurRepo,
            AlertRepository alertRepo,
            MaintenanceRepository maintenanceRepo,
            TrajetRepository trajetRepo,
            PositionRepository positionRepo,
            RedisTemplate<String, String> redisTemplate) {
        return args -> {
            if (vehicleRepo.count() > 0) {
                return;
            }

            Client client = new Client();
            client.setNom("Sofrecom Tunisia");
            client.setEmail("ops@sofrecom.tn");
            client = clientRepo.save(client);

            Vehicle vehicle1 = new Vehicle();
            vehicle1.setNom("VEH-011");
            vehicle1.setMatricule("TN-2024-001");
            vehicle1.setModele("Mercedes Actros");
            vehicle1.setType("Camion");
            vehicle1.setStatut("ACTIF");
            vehicle1.setImei("359123456789012");
            vehicle1.setClient(client);

            Vehicle vehicle2 = new Vehicle();
            vehicle2.setNom("VEH-022");
            vehicle2.setMatricule("TN-2024-002");
            vehicle2.setModele("Renault Master");
            vehicle2.setType("Van");
            vehicle2.setStatut("MAINTENANCE");
            vehicle2.setImei("359123456789013");
            vehicle2.setClient(client);

            Vehicle vehicle3 = new Vehicle();
            vehicle3.setNom("VEH-033");
            vehicle3.setMatricule("TN-2024-003");
            vehicle3.setModele("Peugeot Boxer");
            vehicle3.setType("Voiture");
            vehicle3.setStatut("ARRETE");
            vehicle3.setImei("359123456789014");
            vehicle3.setClient(client);

            Vehicle vehicle4 = new Vehicle();
            vehicle4.setNom("VEH-044");
            vehicle4.setMatricule("TN-2024-004");
            vehicle4.setModele("Iveco Daily");
            vehicle4.setType("Camion");
            vehicle4.setStatut("ACTIF");
            vehicle4.setImei("359123456789015");
            vehicle4.setClient(client);

            Vehicle vehicle5 = new Vehicle();
            vehicle5.setNom("VEH-055");
            vehicle5.setMatricule("TN-2024-005");
            vehicle5.setModele("Ford Transit");
            vehicle5.setType("Van");
            vehicle5.setStatut("ACTIF");
            vehicle5.setImei("359123456789016");
            vehicle5.setClient(client);

            Vehicle vehicle6 = new Vehicle();
            vehicle6.setNom("VEH-066");
            vehicle6.setMatricule("TN-2024-006");
            vehicle6.setModele("Citroën Jumper");
            vehicle6.setType("Bus");
            vehicle6.setStatut("ACTIF");
            vehicle6.setImei("359123456789017");
            vehicle6.setClient(client);

            Vehicle vehicle7 = new Vehicle();
            vehicle7.setNom("VEH-077");
            vehicle7.setMatricule("TN-2024-007");
            vehicle7.setModele("Volvo FH");
            vehicle7.setType("Camion");
            vehicle7.setStatut("ACTIF");
            vehicle7.setImei("359123456789018");
            vehicle7.setClient(client);

            Vehicle vehicle8 = new Vehicle();
            vehicle8.setNom("VEH-088");
            vehicle8.setMatricule("TN-2024-008");
            vehicle8.setModele("Mercedes Sprinter");
            vehicle8.setType("Van");
            vehicle8.setStatut("ACTIF");
            vehicle8.setImei("359123456789019");
            vehicle8.setClient(client);

            Vehicle vehicle9 = new Vehicle();
            vehicle9.setNom("VEH-099");
            vehicle9.setMatricule("TN-2024-009");
            vehicle9.setModele("Peugeot Expert");
            vehicle9.setType("Voiture");
            vehicle9.setStatut("ARRETE");
            vehicle9.setImei("359123456789020");
            vehicle9.setClient(client);

            List<Vehicle> savedVehicles = vehicleRepo.saveAll(
                    List.of(vehicle1, vehicle2, vehicle3, vehicle4, vehicle5, vehicle6, vehicle7, vehicle8, vehicle9));

            ObjectMapper objectMapper = new ObjectMapper();
            for (int i : new int[] { 0, 3, 4, 5, 6, 7 }) {
                Vehicle vehicle = savedVehicles.get(i);
                Map<String, Object> liveData = new HashMap<>();
                liveData.put("vehicleId", vehicle.getId());
                liveData.put("lat", 36.80 + (i * 0.01));
                liveData.put("lng", 10.18 + (i * 0.01));
                liveData.put("speed", 60 + (i * 6));
                liveData.put("temperature", 68 + (i % 3));
                liveData.put("timestamp", LocalDateTime.now().minusMinutes(1).toString());
                redisTemplate.opsForValue().set(
                        "vehicle:live:" + vehicle.getId(),
                        objectMapper.writeValueAsString(liveData));
            }

            Chauffeur chauffeur1 = new Chauffeur();
            chauffeur1.setNom("Ben Ali");
            chauffeur1.setPrenom("Mohamed");
            chauffeur1.setTelephone("55000001");
            chauffeur1.setPermis("TN-2019-001");
            chauffeur1.setVehicle(savedVehicles.get(0));

            Chauffeur chauffeur2 = new Chauffeur();
            chauffeur2.setNom("Khaled");
            chauffeur2.setPrenom("Samir");
            chauffeur2.setTelephone("55000002");
            chauffeur2.setPermis("TN-2019-002");
            chauffeur2.setVehicle(savedVehicles.get(1));

            Chauffeur chauffeur3 = new Chauffeur();
            chauffeur3.setNom("Mrad");
            chauffeur3.setPrenom("Nabil");
            chauffeur3.setTelephone("55000003");
            chauffeur3.setPermis("TN-2019-003");
            chauffeur3.setVehicle(savedVehicles.get(2));
            chauffeurRepo.saveAll(List.of(chauffeur1, chauffeur2, chauffeur3));

            Alert alert1 = new Alert();
            alert1.setVehicle(savedVehicles.get(0));
            alert1.setType("SPEED");
            alert1.setValeur(128.4);
            alert1.setTimestamp(LocalDateTime.now().minusMinutes(15));

            Alert alert2 = new Alert();
            alert2.setVehicle(savedVehicles.get(1));
            alert2.setType("TEMPERATURE");
            alert2.setValeur(91.2);
            alert2.setTimestamp(LocalDateTime.now().minusMinutes(8));

            Alert alert3 = new Alert();
            alert3.setVehicle(savedVehicles.get(2));
            alert3.setType("GEOFENCE");
            alert3.setValeur(1.0);
            alert3.setTimestamp(LocalDateTime.now().minusMinutes(3));
            alertRepo.saveAll(List.of(alert1, alert2, alert3));

            Maintenance maintenance1 = new Maintenance();
            maintenance1.setVehicle(savedVehicles.get(1));
            maintenance1.setType("Vidange");
            maintenance1.setDateIntervention(LocalDateTime.now().minusDays(2));
            maintenance1.setKilometrage(45000.0);
            maintenance1.setProchainKm(50000.0);
            maintenance1.setNotes("Révision périodique");

            Maintenance maintenance2 = new Maintenance();
            maintenance2.setVehicle(savedVehicles.get(0));
            maintenance2.setType("Freinage");
            maintenance2.setDateIntervention(LocalDateTime.now().minusDays(5));
            maintenance2.setKilometrage(62000.0);
            maintenance2.setProchainKm(68000.0);
            maintenance2.setNotes("Plaquettes remplacées");
            maintenanceRepo.saveAll(List.of(maintenance1, maintenance2));

            Trajet trajet1 = new Trajet();
            trajet1.setVehicle(savedVehicles.get(0));
            trajet1.setStartTime(LocalDateTime.now().minusHours(2));
            trajet1.setEndTime(LocalDateTime.now().minusHours(1));
            trajet1.setDistanceKm(142.2);
            trajet1.setConsommationLitres(18.5);

            Trajet trajet2 = new Trajet();
            trajet2.setVehicle(savedVehicles.get(1));
            trajet2.setStartTime(LocalDateTime.now().minusHours(5));
            trajet2.setEndTime(LocalDateTime.now().minusHours(3));
            trajet2.setDistanceKm(88.0);
            trajet2.setConsommationLitres(9.8);

            Trajet trajet3 = new Trajet();
            trajet3.setVehicle(savedVehicles.get(2));
            trajet3.setStartTime(LocalDateTime.now().minusHours(8));
            trajet3.setEndTime(LocalDateTime.now().minusHours(7));
            trajet3.setDistanceKm(64.0);
            trajet3.setConsommationLitres(7.4);
            trajetRepo.saveAll(List.of(trajet1, trajet2, trajet3));

            Position position1 = new Position();
            position1.setVehicle(savedVehicles.get(0));
            position1.setLat(36.8065);
            position1.setLng(10.1815);
            position1.setSpeed(78.2);
            position1.setTemperature(67.1);
            position1.setTimestamp(LocalDateTime.now().minusMinutes(2));

            Position position2 = new Position();
            position2.setVehicle(savedVehicles.get(1));
            position2.setLat(36.8192);
            position2.setLng(10.1653);
            position2.setSpeed(54.8);
            position2.setTemperature(72.6);
            position2.setTimestamp(LocalDateTime.now().minusMinutes(4));

            Position position3 = new Position();
            position3.setVehicle(savedVehicles.get(2));
            position3.setLat(36.8509);
            position3.setLng(10.3248);
            position3.setSpeed(21.4);
            position3.setTemperature(69.3);
            position3.setTimestamp(LocalDateTime.now().minusMinutes(6));
            positionRepo.saveAll(List.of(position1, position2, position3));
        };
    }
}
