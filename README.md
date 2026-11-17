# FlySync – Flight Aggregation System

A Spring Boot REST API that aggregates flight search results from multiple airline providers, applies customer-selected filters, and maintains flight search history.

## Overview

FlySync simplifies flight discovery by allowing customers to search using standardized airport codes after looking up airports by city or airport name.

The system queries multiple airline providers concurrently, converts their results into a common response format, combines the results, and applies filters such as maximum price, airline, departure time, and sorting preference.

**Note:** The current airline integrations use simulated inventory and sample flight data. They are not connected to live airline booking systems or real-time airline APIs.

## Features

- **Airport Search:** Search airports by city, airport name, country, or IATA code.
- **Multi-Airline Aggregation:** Collect flight results from multiple airline adapters.
- **Concurrent Execution:** Uses Java `CompletableFuture` to query providers concurrently.
- **Adapter Pattern:** Normalizes provider-specific results into a common flight response.
- **Flight Filtering:** Filter by maximum price, airline, and departure time.
- **Sorting:** Sort flights by price in ascending or descending order.
- **Fault Tolerance:** Handles individual provider failures and timeouts.
- **Search History:** Stores previous flight searches in PostgreSQL.
- **Request Validation:** Validates airport codes, dates, passenger counts, and filters.
- **Global Exception Handling:** Returns structured JSON error responses.
- **Interactive API Documentation:** Swagger UI for exploring and testing endpoints.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot | Backend application framework |
| Spring Web | REST API development |
| Spring JDBC / JdbcTemplate | Database operations |
| PostgreSQL | Relational database |
| Maven | Dependency and build management |
| Jakarta Bean Validation | Request validation |
| CompletableFuture | Concurrent provider execution |
| JUnit 5 | Unit testing |
| Mockito | Mocking dependencies in tests |
| Springdoc OpenAPI | Swagger API documentation |
| IntelliJ IDEA | Development environment |

## Architecture

```text
                    Customer
                       |
                       v
                 REST Controllers
                       |
          +------------+------------+
          |                         |
          v                         v
    Airport Service            Flight Service
          |                         |
          v                         v
    Airport Repository       Airline Adapters
          |                  /             \
          v                 v               v
      PostgreSQL         IndiGo          Air India
                            \               /
                             \             /
                              v           v
                         Flight Aggregation
                                 |
                                 v
                          Apply Filters
                                 |
                                 v
                         Flight Response
                                 |
                                 v
                       Search History
                                 |
                                 v
                            PostgreSQL
```

## Flight Search Workflow

1. The customer searches for an airport using its city or name.
2. The Airport API returns matching airports and their IATA codes.
3. The customer selects source and destination airports.
4. The Flight API validates the search request and airport codes.
5. FlightService invokes the configured airline adapters concurrently.
6. Each adapter retrieves its available sample flights and maps them into the common `FlightResponse` format.
7. The service aggregates results and handles provider failures.
8. Customer-selected filters and sorting are applied.
9. The response is returned with an appropriate status.
10. The search request and result count are saved in PostgreSQL.

## API Endpoints

Base URL: `http://localhost:8080`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/flights/test` | Check application availability |
| GET | `/api/v1/airports/search?keyword=Kochi` | Search airports |
| POST | `/api/v1/flights/search` | Search and filter flights |
| GET | `/api/v1/flights/history` | Retrieve search history |

### Sample Flight Search Request

`POST /api/v1/flights/search`

```json
{
  "source": "COK",
  "destination": "DEL",
  "departureDate": "2026-12-15",
  "passengers": 1,
  "filter": {
    "maxPrice": 5000,
    "sortBy": "PRICE_ASC"
  }
}
```

### Possible Search Statuses

| Status | Meaning |
|---|---|
| SUCCESS | All configured providers responded successfully |
| PARTIAL_SUCCESS | Some providers failed, but results are available |
| NO_FLIGHTS | No flights match the requested route and filters |
| UNAVAILABLE | No provider is configured or all providers failed |

## Database

FlySync uses PostgreSQL to persist:

- Airport information, including airport name, city, country, and IATA code.
- Flight search history, including route, departure date, passenger count, search status, result count, and search timestamp.

Database schema and initial airport records are maintained in:

- `src/main/resources/schema.sql`
- `src/main/resources/data.sql`

## Setup and Execution

### Prerequisites

- Java 21
- Maven
- PostgreSQL
- IntelliJ IDEA or another Java IDE

### 1. Clone the repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd FlySync
```

### 2. Configure PostgreSQL

Create a PostgreSQL database for FlySync.

Configure the database connection in:

`src/main/resources/application.properties`

Use your own database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/flysync
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.sql.init.mode=always
```

### 3. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

Alternatively, run `FlySyncApplication.java` from IntelliJ IDEA.

The application starts at:

`http://localhost:8080`

### 4. Open Swagger UI

`http://localhost:8080/swagger-ui/index.html`

Use Swagger to explore and execute the available API endpoints.

## Testing

The project includes unit tests using JUnit 5 and Mockito.

Current tests cover:

- Invalid source airport rejection.
- Invalid destination airport rejection.
- Handling an empty airline provider list.
- Maximum-price filtering and descending price sorting.
- Aggregating results from multiple airline providers.
- Partial success when one airline provider fails.

Run tests using:

```bash
mvn test
```

## Design Patterns and Engineering Concepts

- **Adapter Pattern:** Provides a common interface for different airline providers.
- **Dependency Injection:** Spring manages service and adapter dependencies.
- **Asynchronous Programming:** `CompletableFuture` enables concurrent provider searches.
- **Fault Tolerance:** Provider-level exception handling and timeouts prevent one failed provider from blocking all results.
- **Layered Architecture:** Separates controllers, services, repositories, adapters, DTOs, and models.
- **Global Exception Handling:** Centralizes REST API error responses.

## Current Limitations

- Airline data is simulated using predefined sample inventories.
- No live airline API integrations are configured.
- No booking, payment, or ticket issuance functionality is implemented.
- Airport records are currently populated through seed data.
- Search history is persisted, but user authentication and individual user accounts are not implemented.

## Future Enhancements

- Integrate real airline or flight-data APIs.
- Add user authentication and personalized search history.
- Introduce caching for frequently searched routes.
- Add pagination for search history and flight results.
- Expand airport data and support additional locations.
- Add integration testing and deployment automation.

---

**Built with Java 21, Spring Boot, and PostgreSQL.**