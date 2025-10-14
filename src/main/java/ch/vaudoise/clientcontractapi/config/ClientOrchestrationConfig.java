package ch.vaudoise.clientcontractapi.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.vaudoise.clientcontractapi.services.handlers.ClientHandler;
import ch.vaudoise.clientcontractapi.models.enums.ClientType;

/**
 * Configuration class for client orchestration.
 * It provides beans related to client handling logic.
 */
@Configuration
public class ClientOrchestrationConfig {

    /**
     * Creates a map of ClientHandler instances keyed by their supported ClientType.
     * 
     * @param handlerList the list of ClientHandler instances available in the context
     * @return a map where the key is the ClientType and the value is the corresponding ClientHandler
     */
    @Bean
    public Map<ClientType, ClientHandler<?, ?>> clientHandlers(
            List<ClientHandler<?, ?>> handlerList) {
        return handlerList.stream()
                .collect(Collectors.toMap(ClientHandler::getSupportedClientType, h -> h));
    }
}
