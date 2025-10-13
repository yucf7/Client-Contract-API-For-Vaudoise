package ch.vaudoise.clientcontractapi.mappers;

import ch.vaudoise.clientcontractapi.dtos.ContractDTO;
import ch.vaudoise.clientcontractapi.models.entities.Contract;
import ch.vaudoise.clientcontractapi.models.entities.client.Client;
import ch.vaudoise.clientcontractapi.models.entities.client.Person;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "client", source = "clientId")
    Contract toEntity(ContractDTO dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "client.id", target = "clientId")
    ContractDTO toDTO(Contract entity);

    default Client map(Long clientId) {
        if (clientId == null) {
            return null;
        }
        Person client = new Person();
        client.setId(clientId);
        return client;
    }

    default Long map(Client client) {
        return client != null ? client.getId() : null;
    }
}
