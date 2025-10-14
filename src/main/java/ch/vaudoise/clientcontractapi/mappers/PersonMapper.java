package ch.vaudoise.clientcontractapi.mappers;

import ch.vaudoise.clientcontractapi.dtos.client.PersonDTO;
import ch.vaudoise.clientcontractapi.models.entities.client.Person;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "birthdate", source = "birthdate")
    PersonDTO toDTO(Person person);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "type", source = "type")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "birthdate", source = "birthdate")
    Person toEntity(PersonDTO personDTO);

    default UUID map(String id) {
        return id == null ? null : UUID.fromString(id);
    }

    default String map(UUID id) {
        return id == null ? null : id.toString();
    }

}