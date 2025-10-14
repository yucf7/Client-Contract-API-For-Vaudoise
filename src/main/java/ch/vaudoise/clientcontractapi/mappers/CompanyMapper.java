package ch.vaudoise.clientcontractapi.mappers;

import ch.vaudoise.clientcontractapi.dtos.client.CompanyDTO;
import ch.vaudoise.clientcontractapi.models.entities.client.Company;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "type", source = "type")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "companyIdentifier", source = "companyIdentifier")
    CompanyDTO toDTO(Company company);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "type", source = "type")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "companyIdentifier", source = "companyIdentifier")
    Company toEntity(CompanyDTO companyDTO);

    default UUID map(String id) {
        return id == null ? null : UUID.fromString(id);
    }

    default String map(UUID id) {
        return id == null ? null : id.toString();
    }

}