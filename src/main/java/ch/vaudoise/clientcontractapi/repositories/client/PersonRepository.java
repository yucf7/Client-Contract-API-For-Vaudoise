package ch.vaudoise.clientcontractapi.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.vaudoise.clientcontractapi.models.entities.client.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
