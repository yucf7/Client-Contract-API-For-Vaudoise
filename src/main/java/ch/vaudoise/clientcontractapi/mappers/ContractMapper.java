package ch.vaudoise.clientcontractapi.mappers;

import ch.vaudoise.clientcontractapi.dtos.ContractDTO;
import ch.vaudoise.clientcontractapi.models.entities.Contract;
import ch.vaudoise.clientcontractapi.models.entities.client.Client;
import ch.vaudoise.clientcontractapi.models.entities.client.Person;

import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "client", source = "clientId")
    Contract toEntity(ContractDTO dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "costAmount", target = "costAmount")
    ContractDTO toDTO(Contract entity);

    default UUID map(String id) {
        return id == null ? null : UUID.fromString(id);
    }

    default Client map(UUID clientId) {
        if (clientId == null) {
            return null;
        }
        Person client = new Person();
        client.setId(clientId);
        return client;
    }
}
