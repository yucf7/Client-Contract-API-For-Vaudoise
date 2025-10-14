package ch.vaudoise.clientcontractapi.models.enums;

import java.util.Optional;

public class EnumUtils {

    /**
     * Converts a string to ClientType enum safely.
     * Returns Optional.empty() if no matching enum found.
     */
    public static Optional<ClientType> fromString(String typeStr) {
        if (typeStr == null || typeStr.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(ClientType.valueOf(typeStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Converts a ClientType enum to string.
     * Returns empty string if enum is null.
     */
    public static String toString(ClientType clientType) {
        return clientType == null ? "" : clientType.name();
    }
}
