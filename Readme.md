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

---

## 📁 Project Structure

```
src/main/java/ch/vaudoise/clientcontractapi/
│
├── ClientContractApiApplication.java       # Spring Boot entry point
│
├── config/
│   └── OpenApiConfig.java                  # Swagger/OpenAPI configuration
│
├── dtos/
│   ├── ContractDTO.java
│   └── client/
│       ├── ClientDTO.java
│       ├── CompanyDTO.java
│       └── PersonDTO.java
│
├── mappers/
│   ├── CompanyMapper.java
│   ├── ContractMapper.java
│   └── PersonMapper.java
│
├── models/
│   ├── entities/
│   │   ├── Contract.java
│   │   └── client/
│   │       ├── Client.java
│   │       ├── Company.java
│   │       └── Person.java
│   └── enums/
│       └── ClientType.java
│
└── repository/
    ├── client/
    │   ├── ClientRepository.java
    │   ├── PersonRepository.java
    │   └── CompanyRepository.java
    └── ContractRepository.java
     
```

---

## ⚙️ Build & Run

Make sure you have **Java 17+** and **Maven** installed.

### Build
```bash
./mvnw clean package
```

### Run
```bash
./mvnw spring-boot:run
```

---

## 📚 API Documentation

Once running, the API documentation (Swagger UI) is available at:

```
http://localhost:8080/swagger-ui.html
```

---

## 🚧 Current Progress

✅ Implemented so far:
- Project setup & configuration  
- Entity and DTO structure  
- MapStruct mappers  
- Database migration (Flyway)  
- Repository layer  

🔜 Next steps:
- Implement REST controllers  
- Add service layer and business logic  
- Integrate validation (email, phone, ISO dates)  
- Write integration & unit tests  
- Optimize query performance (especially cost sum endpoint)

---

## 👤 Author

**Developed by:** Youssef FRIKHAT  
**Github:** [yucf7](https://github.com/yucf7)

**License:** Apache 2.0
