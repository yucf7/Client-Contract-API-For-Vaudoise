package ch.vaudoise.clientcontractapi.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;

@Converter(autoApply = true)
public class ClientTypeConverter implements AttributeConverter<ClientType, String> {

    @Override
    public String convertToDatabaseColumn(ClientType attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public ClientType convertToEntityAttribute(String dbData) {
        return dbData != null ? ClientType.valueOf(dbData) : null;
    }
}
