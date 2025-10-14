package ch.vaudoise.clientcontractapi.repositories.client;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.vaudoise.clientcontractapi.models.entities.client.Person;

/**
 * Repository interface for {@link Person} entity.
 * Provides database operations for retrieving and manipulating person records.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {
    // all CRUD operations are provided by JpaRepository.
}
