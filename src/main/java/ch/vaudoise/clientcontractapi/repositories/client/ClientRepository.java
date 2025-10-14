package ch.vaudoise.clientcontractapi.repositories.client;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.vaudoise.clientcontractapi.models.entities.client.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    
}