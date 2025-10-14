package ch.vaudoise.clientcontractapi.services.handlers;

import ch.vaudoise.clientcontractapi.dtos.client.ClientDTO;
import ch.vaudoise.clientcontractapi.dtos.client.CompanyDTO;
import ch.vaudoise.clientcontractapi.mappers.CompanyMapper;
import ch.vaudoise.clientcontractapi.models.entities.client.Company;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;
import ch.vaudoise.clientcontractapi.services.client.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Strategy implementation of {@link ClientHandler} for managing {@link Company} clients.
 * <p>
 * This handler performs CRUD operations specific to the {@link ClientType#COMPANY} type,
 * using {@link CompanyService} for business logic and {@link CompanyMapper} for DTO-entity conversion.
 */
@Service
@RequiredArgsConstructor
public class CompanyHandler implements ClientHandler<Company, CompanyDTO> {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    @Override
    public ClientType getSupportedClientType() {
        return ClientType.COMPANY;
    }

    /**
     * Returns the client type handled by this implementation.
     *
     * @return {@link ClientType#COMPANY}
     */
    @Override
    public ClientType getClientType() {
        return ClientType.COMPANY;
    }

    /**
     * Retrieves all {@link Company} clients and maps them to {@link CompanyDTO}.
     *
     * @return a list of {@link CompanyDTO} instances
     */
    @Override
    public List<CompanyDTO> getAll() {
        return companyService.getAllCompanies().stream()
                .map(companyMapper::toDTO)
                .toList();
    }

    /**
     * Retrieves a {@link Company} by ID and maps it to {@link CompanyDTO}.
     *
     * @param id the ID of the company to retrieve
     * @return the corresponding {@link CompanyDTO}, or {@code null} if not found
     */
    @Override
    public CompanyDTO getById(String id) {
        UUID uuid = toUUID(id);
        return companyService.getCompanyById(uuid)
                .map(companyMapper::toDTO)
                .orElse(null);
    }

    /**
     * Creates a new {@link Company} from the given {@link CompanyDTO}.
     *
     * @param dto the DTO containing company data
     * @return the created {@link CompanyDTO}
     */
    @Override
    public CompanyDTO create(CompanyDTO dto) {
        Company entity = companyMapper.toEntity(dto);
        return companyMapper.toDTO(companyService.createCompany(entity));
    }

    /**
     * Updates an existing {@link Company} with the given ID using the provided {@link CompanyDTO}.
     *
     * @param id  the ID of the company to update
     * @param dto the DTO containing updated company data
     * @return the updated {@link CompanyDTO}, or {@code null} if not found
     */
    @Override
    public CompanyDTO update(String id, CompanyDTO dto) {
        UUID uuid = toUUID(id);
        return companyService.getCompanyById(uuid)
                .map(existing -> companyMapper.toDTO(
                        companyService.updateCompany(existing, companyMapper.toEntity(dto))))
                .orElse(null);
    }

    /**
     * Deletes a {@link Company} client by its ID.
     *
     * @param id the ID of the company to delete
     */
    @Override
    public void delete(String id) {
        UUID uuid = toUUID(id);
        companyService.getCompanyById(uuid).ifPresent(companyService::deleteCompany);
    }

    /**
     * Converts a generic {@link ClientDTO} into a {@link CompanyDTO}.
     *
     * @param genericDto the generic DTO to convert
     * @return the specific {@link CompanyDTO}
     * @throws IllegalArgumentException if the DTO is not an instance of {@link CompanyDTO}
     */
    @Override
    public CompanyDTO convertToSpecificDto(ClientDTO genericDto) {
        if (!(genericDto instanceof CompanyDTO)) {
            throw new IllegalArgumentException("Expected CompanyDTO but got: " + genericDto.getClass().getSimpleName());
        }
        return (CompanyDTO) genericDto;
    }

    /**
     * Utility method to safely convert String to UUID.
     * Throws IllegalArgumentException if invalid.
     *
     * @param id the string representation of UUID
     * @return the UUID object
     */
    private UUID toUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid UUID string: " + id, ex);
        }
    }
}
