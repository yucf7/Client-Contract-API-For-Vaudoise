package ch.vaudoise.clientcontractapi.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI clientContractApi() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Client & Contract API")
                                                .description("REST API for managing clients and their contracts for Vaudoise.")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Youssef FRIKHAT")
                                                                .url("https://github.com/yucf7"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("https://springdoc.org")))
                                .externalDocs(new ExternalDocumentation()
                                                .description("GitHub Repository")
                                                .url("https://github.com/yucf7/Client-Contract-API-For-Vaudoise"));
        }
}
