# Direct Debit Service 🏦

<div align="center">

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12%2B-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

A modern, secure, and scalable direct debit management system built with Spring Boot 3.x and PostgreSQL.

[Key Features](#-features) • [Quick Start](#-quick-start) • [Documentation](#-documentation)

</div>

## 📋 Table of Contents

- [Features](#-features)
- [Quick Start](#-quick-start)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Documentation](#-documentation)
- [Technology Stack](#️-technology-stack)
- [Project Structure](#-project-structure)
- [Security](#-security)
- [API Reference](#-api-reference)
- [Development](#-development)
- [License](#-license)
- [Authors](#-authors)

## 🌟 Features

- **🔐 Secure Authentication**
  - OAuth2 resource server implementation
  - JWT token authentication

- **💾 Database Management**
  - PostgreSQL with Flyway migrations
  - Automated schema versioning
  - Data integrity protection

- **📚 API Documentation**
  - OpenAPI/Swagger UI integration
  - Detailed endpoint documentation

- **🔄 Data Persistence**
  - Spring Data JPA
  - Efficient database operations
  - Transaction management

- **📝 Direct Debit Mandate Management**
  - Electronic mandate creation and validation
  - Provider-specific mandate flows
  - OAuth2 token management for providers
  - Automatic mandate state management
  - Real-time mandate status tracking
  - Timeout handling for incomplete mandates

### Mandate Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant DD as Direct Debit Service
    participant P as Provider
    
    Note over C,DD: Initial Creation (INITIAL)
    C->>DD: Create mandate (user_id)
    DD-->>C: Return mandate reference
    
    Note over C,DD: Details Update (DRAFT)
    C->>DD: Update mandate details & provider
    DD-->>C: Confirmation
    
    Note over C,DD: Redirect Request (SENT)
    C->>DD: Request redirect URL
    alt Provider Requires Pre-submission
        DD->>P: Submit mandate
        P-->>DD: Mandate registered
    end
    DD-->>C: Return provider redirect URL
    
    Note over C,P: Provider Authorization
    C->>P: Redirect to provider portal
    
    alt Successful Flow
        Note over P,DD: Callback Processing (WAITING_FOR_VERIFICATION)
        P-->>DD: Authorization callback (success)
        Note over DD,P: Token Exchange
        DD->>P: Exchange auth token for access token
        P-->>DD: Access token
        Note over DD: Status Update (ACTIVE)
        DD->>DD: Update mandate status to ACTIVE
        DD-->>C: Mandate activation notification
    else Error Flow
        Note over P,DD: Error Handling
        P-->>DD: Authorization callback (error)
        Note over DD: Status Update (CANCELLED/FAILED)
        DD->>DD: Update status to CANCELLED/FAILED
    end

    Note over DD: Background Processes
    par Stale Mandate Check
        Note over DD: Not implemented yet 
        Timeout Check
            DD->>DD: Check for stale mandates
            alt Mandate inactive > 30 sec
            Note over DD: Status Update (FAILED)
            DD->>DD: Update status to FAILED
        end
    and Future: Expiration Check
        Note over DD: Not implemented yet
        loop Periodic Check
            Note over DD: Expiration Check
            DD->>DD: Check mandate expiration
            alt Mandate expired
                Note over DD: Status Update (EXPIRED)
                DD->>DD: Update status to EXPIRED
            end
        end
    end
```

### Mandate States and Flow Details

1. **Initial Creation**
   - Client requests mandate creation with user ID
   - Service creates mandate record and returns reference
   - Initial State: `INITIAL`

2. **Details Update**
   - Client provides mandate details and provider information
   - Service validates and updates mandate
   - State: `DRAFT`

3. **Provider Authorization**
   - Client requests redirect URL
   - For some providers: Pre-submission of mandate
   - User is redirected to provider's portal
   - State: `SENT`

4. **Provider Callback**
   - Provider calls back with authorization token
   - State: `WAITING_FOR_VERIFICATION`
   - Service exchanges auth token for access token

5. **Mandate Activation**
   - Service stores access token
   - Mandate becomes active
   - Final State: `ACTIVE`

6. **Error States**
   - `FAILED`: 
     - Automatic timeout after 30 seconds of inactivity
     - Failed token exchange
     - Provider returns error in callback
   - `CANCELLED`:
     - Provider returns cancellation in callback
     - User cancels during provider authorization
   - `EXPIRED`:
     - Mandate has reached its expiration date *(Not implemented yet)*

### Background Jobs *(Not implemented yet)*
1. **Stale Mandate Check**
   - Runs every 30 seconds
   - Marks inactive mandates as FAILED

2. **Expiration Check** *(Not implemented yet)*
   - Will periodically check mandate expiration dates
   - Will mark expired mandates as EXPIRED

### Coming Soon
- Mandate expiration management
- Payment flow implementation
- Provider transfer flows
- Enhanced mandate management features

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### Installation

1. **Clone the repository:**
```bash
git clone https://github.com/arefPi/direct.debit.git
cd direct-debit
```

2. **Configure database:**
`application.yml` in `src/main/resources`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/direct_debit
    username: your_username
    password: your_password
```

3. **Build and run:**
```bash
./mvnw clean install
./mvnw spring-boot:run
```

Visit [http://localhost:8080](http://localhost:8080) 🎉

## 📚 Documentation

### API Documentation
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

## 🛠️ Technology Stack

<div align="center">

| Category | Technology |
|----------|------------|
| Framework | Spring Boot 3.4.3 |
| Security | Spring Security, OAuth2 |
| Database | PostgreSQL, Flyway |
| Documentation | SpringDoc OpenAPI |
| Build Tool | Maven |
| Language | Java 17 |
| Utilities | Lombok, MapStruct |

</div>

## 📁 Project Structure

```
direct-debit/
├── src/
│   └── main/
│       └── java/
│           ├── presentation/           # Presentation Layer
│           │   ├── controller/         # REST Controllers
│           │   ├── dto/               # Data Transfer Objects
│           │   └── mapper/            # DTO-Entity Mappers
│           │
│           ├── service/               # Service Layer
│           │   ├── impl/             # Service Implementations
│           │   ├── model/            # Domain Models
│           │   └── exception/        # Business Exceptions
│           │
│           └── persistence/          # Persistence Layer
│               ├── repository/       # Data Access Layer
│               ├── entity/          # Database Entities
│               └── mapper/          # Entity Mappers
│
├── resources/
│   └── db/
│       └── migration/               # Flyway Migrations
│
└── pom.xml                         # Project Dependencies
```

## 🔒 Security

- **Authentication**: OAuth2 Resource Server with JWT

## 💻 Development

### Commit Messages

```
feat: Add new feature
fix: Fix bug
refactor: Refactor code
test: Add tests
```


## 👥 Authors

- **Aref Parhizkari** - [@arefPi](https://github.com/arefPi)

---

<div align="center">

⭐️ If you find this project useful, please consider giving it a star!

</div> 