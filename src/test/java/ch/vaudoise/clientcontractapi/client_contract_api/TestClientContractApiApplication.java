package ch.vaudoise.clientcontractapi.client_contract_api;

import org.springframework.boot.SpringApplication;

import ch.vaudoise.clientcontractapi.ClientContractApiApplication;

public class TestClientContractApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(ClientContractApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
