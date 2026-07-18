package com.sofrecom.fleetmanagement.Repository;

import com.sofrecom.fleetmanagement.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepo extends JpaRepository<Client, Long> {
}