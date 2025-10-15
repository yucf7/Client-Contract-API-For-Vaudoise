# 🧩 Client Contract API

A **Spring Boot REST API** designed to manage **clients** and their **contracts** in an insurance context.  
The application exposes endpoints to create, read, update, and delete clients (persons or companies), as well as to manage their active contracts.

---

## 🏗️ Architecture Overview

The project follows a **clean layered architecture** ensuring modularity, maintainability, and testability:

- **Spring Boot Application** — Main entry point: `ClientContractApiApplication`.
- **OpenAPI / Swagger Configuration** — Via `OpenApiConfig` for auto-generated REST documentation.
- **Entities (Domain Model)**  
  - `Client` *(abstract base)* with subtypes: `Person` and `Company`.  
  - `Contract` linked to a `Client`.
- **DTOs (Data Transfer Objects)**  
  - `ClientDTO` as a base DTO.  
  - Specialized DTOs: `PersonDTO`, `CompanyDTO`.  
  - `ContractDTO` for contract data transfer.
- **Mappers (MapStruct)**  
  - `PersonMapper`, `CompanyMapper`, `ContractMapper` — handle conversion between entities and DTOs.
- **Services & Handlers (Strategy Pattern)**  
  - Encapsulate client-type-specific logic for `Person` and `Company` via `ClientHandler` implementations.
- **Repositories (Spring Data JPA)**  
  - Abstract persistence layer using `JpaRepository` interfaces.
- **Validation Layer**  
  - Custom annotations and validators (`@ValidEndDate`, `@ValidCompanyIdentifier`).
- **Testing Layer**  
  - Unit and integration tests with JUnit 5, Mockito, and Testcontainers.

---

## 📁 Project Structure
```bash
src/main/java/ch/vaudoise/clientcontractapi/
│
├── ClientContractApiApplication.java # Spring Boot entry point
│
├── config/
│ └── OpenApiConfig.java # Swagger/OpenAPI configuration
│
├── controllers/ # REST endpoints
│ ├── ClientController.java
│ └── ContractController.java
│
├── dtos/
│ ├── ContractDTO.java
│ └── client/
│ ├── ClientDTO.java
│ ├── CompanyDTO.java
│ └── PersonDTO.java
│
├── mappers/
│ ├── CompanyMapper.java
│ ├── ContractMapper.java
│ └── PersonMapper.java
│
├── models/
│ ├── entities/
│ │ ├── Contract.java
│ │ └── client/
│ │ ├── Client.java
│ │ ├── Company.java
│ │ └── Person.java
│ └── enums/
│ └── ClientType.java
│
├── services/
│ ├── ContractService.java
│ └── client/
│ ├── ClientOrchestrationService.java
│ ├── ClientService.java
│ ├── PersonService.java
│ └── CompanyService.java
│
├── handlers/ # Strategy pattern implementations
│ ├── ClientHandler.java
│ ├── PersonHandler.java
│ └── CompanyHandler.java
│
└── repositories/
├── ContractRepository.java
└── client/
├── ClientRepository.java
├── PersonRepository.java
└── CompanyRepository.java
```
---

## ⚙️ Build & Run Locally

### ✅ Prerequisites
- **Java 17+**
- **Maven 3.9+**
- **PostgreSQL** (or Docker if you prefer containers)

### 🧱 Build
```bash
./mvnw clean package
```
### ▶️ Run
```bash
./mvnw spring-boot:run
```
This will start the app at:

``` bash
http://localhost:8080/swagger-ui/index.html
```

### 🧪 Running Tests

This project includes unit and integration tests covering:

Services (business logic)

Controllers (REST endpoints)

Validation and error handling

#### Run all tests:
``` bash
./mvnw test
```
#### Run with detailed logs:

``` bash
./mvnw test -Dspring.profiles.active=test -Dlogging.level.root=DEBUG
```

All test reports are generated under:

``` bash
target/surefire-reports/
```

Integration tests use **Testcontainers** to spin up isolated PostgreSQL instances automatically so no manual DB setup required for CI/CD.

### 🐳 Docker Setup

#### 🧰 Build the Docker image
``` bash
docker build -t client-contract-api .
```

#### 🚀 Run with Docker Compose
``` bash
docker-compose up --build
```

This will:

Start a PostgreSQL container with client_contract_db

Launch the Spring Boot API container connected to it

Apply Flyway migrations automatically

## 🧹 Stop and clean containers

``` bash
docker-compose down -v
```

### 🚧 Current Progress

#### Implemented so far:
- Project setup & configuration
- Entity and DTO structure
- MapStruct mappers
- Database migration (Flyway)
- Repository layer
- Service layer (Person, Company, Contract)
- REST controllers with proper validation
- Global exception handling
- Unit & integration tests (JUnit, Mockito, Testcontainers)
- Docker & Docker Compose setup for local development

### 🧠 Key Technical Highlights
- Clean architecture & layered design, isolates domain logic from presentation and persistence.
- Strategy Pattern for client-type-specific orchestration (Company vs Person).
- MapStruct for efficient and type-safe DTO ↔ Entity conversions.
- Validation Layer with custom annotations.
- Flyway for database version control.
- OpenAPI (Swagger) for automatic documentation.
- Testcontainers ensures reproducible and environment-independent tests.
- Dockerized for full local and CI/CD deploy parity.

#### 👤 Author

**Developed by:** Youssef FRIKHAT  
**Github:** [yucf7](https://github.com/yucf7)

**License:** Apache 2.0