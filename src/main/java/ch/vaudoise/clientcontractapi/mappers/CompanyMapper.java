package ch.vaudoise.clientcontractapi.mappers;

import ch.vaudoise.clientcontractapi.dtos.client.CompanyDTO;
import ch.vaudoise.clientcontractapi.models.entities.client.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    
    CompanyDTO toDTO(Company company);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    Company toEntity(CompanyDTO companyDTO);
}
