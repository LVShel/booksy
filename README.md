# Booksy - Online Bookstore Backend

Welcome to **Booksy**, a simple REST API for managing a bookstore inventory, purchases, and customer loyalty points.

---

##  Features

-  **Inventory Management**: Add, update, delete, and list books
-  **Storefront**: Purchase books with automatic pricing strategies
-  **Loyalty System**: Earn points and get discounts or free books
-  **Discount Strategies**: Based on book types and bundle sizes
-  **Scheduled Job**: Auto-mark old books as "TO_BE_REMOVED" based on age
-  **Unit Tests**: Service layer covered with JUnit 5 tests
-  **Dockerized**: Ready to run anywhere with one simple command

---

##  API Overview

All APIs are organized into these groups:

| Tag | Purpose |
|:----|:--------|
| `inventory` | Manage books (admin) |
| `storefront` | List available books for customers |
| `purchase` | Preview and execute purchases |
| `customers` | View customer loyalty points |

 Full OpenAPI 3.0 Specification: **`openapi.yaml`** (provided)

Example endpoints:
- `POST /inventory/books` — Add a new book
- `GET /books` — List active books
- `POST /purchase/preview` — Preview total price, discounts, loyalty points
- `POST /purchase` — Finalize a purchase
- `GET /customers/{customerId}/loyalty` — Check loyalty points
 Detailed curl requests examples: **`curl-examples.txt`** (provided)
---

##  Running with Docker

### 1. Build Docker Image

```bash
docker build -t booksy .
```

### 2. Run the Container

```bash
docker run -p 8080:8080 booksy
```

 The application will be available at: [http://localhost:8080](http://localhost:8080)

### 3. Access H2 Console

- URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:bookstoredb`
- Username: `sa`
- Password: (leave empty)

 Note: `webAllowOthers` is enabled to allow Docker external browser access.

---

##  Technologies Used

- **Java 17**
- **Spring Boot 3.4**
- **Gradle 8.5**
- **Lombok**
- **H2 In-Memory Database**
- **JPA (Hibernate)**
- **Docker**
- **AspectJ (for simple logging)**
- **OpenAPI 3.0**

---

## Design Decisions

- **Project Initialization**:  
  The project was generated using [Spring Initializr](https://start.spring.io/) to quickly scaffold a clean Spring Boot 3.4.5 application with Gradle, H2 database, Lombok, and JPA support.

- **Top-Down Design Approach**:  
  The implementation started by designing the OpenAPI 3.0 specification first (using [Swagger Editor](https://editor.swagger.io/)).  
  This allowed to design further the entire backend structure (controllers, services, entities) around API contract.

- **Strategy Pattern** is used for book pricing:
    - Different pricing strategies (`NewReleasePricingStrategy`, `RegularPricingStrategy`, `OldEditionPricingStrategy`) are dynamically chosen based on the book type.
    - This ensures clean separation of pricing rules and makes future expansions (e.g., new book types) easy without touching existing logic.

- **Error Handling and API Consistency**:
    - Introduced a `@RestControllerAdvice` for centralized error handling.
    - Unified error responses using a standard `ApiError` object, to simplify FE integration.

- **Data Validation**:  
  DTO classes are validated with `@Valid`, `@NotNull`, `@NotEmpty`, and a custom `@ValidIsbn` annotation.  
  This prevents bad data from reaching service layers.

- **Book Entity Improvements**:
    - Introduced a unique `isbn` field to prevent duplicate books.
    - Book stock is tracked via a simple `stock` field to simulate Inventory Management for this MVP.

- **Handling Purchase Race Conditions**:  
  Implemented a two-step purchase process:
    - `purchase/preview`: calculate expected price, discounts, and loyalty points.
    - `purchase`: confirm the purchase, re-validate conditions, and complete the transaction atomically.  
      This avoids stale prices, stock conflicts, or loyalty inconsistencies.

- **Transactional Integrity**:  
  `@Transactional` is used for the `purchase` operation to ensure consistency across:
    - Reducing stock
    - Updating customer loyalty points
    - Recording purchase history

- **Scheduled Soft Book Invalidation**:
    - Instead of hard-deleting old books immediately, books are marked with a `TO_BE_REMOVED` status after crossing a configurable threshold.
    - This improves operational safety, allows traceability, and gives admins time to review removals.
    - Added a simple `@Scheduled` Quartz job (`BookInvalidatorJob`) to automatically mark books as `TO_BE_REMOVED` after a configurable time threshold.

- **Simple Loyalty System**:
    - Customers earn 1 point per purchased book.
    - On collecting 10 points, a free book (cheapest among eligible) is granted automatically. Then loyalty points are set back to 0;

- **Configuration Abstraction**:  
  Externalized loyalty-related settings (e.g., enabling/disabling loyalty, max points threshold) using `@ConfigurationProperties`, improving maintainability.

- **Test Data Setup**:  
  `data.sql` is used to preload Books and Customers for easy local testing without manual inserts.

- **Minimal External Dependencies**:
    - H2 database and simple Spring Boot features were chosen to keep the solution portable and easy to run locally or inside Docker without extra services.

---

## Testing

Unit tests are written with **JUnit 5** and run automatically during the Docker build.

You can also run them manually:

```bash
./gradlew test
```


##  Test Coverage

- **Pricing Strategies**:  
  Each pricing rule strategy is covered with unit tests, including edge cases (bundle discounts, loyalty discounts).

- **Loyalty Service**:  
  Loyalty points accumulation are unit tested.

- **Purchase Service**:
    - Successful purchase and preview flows.
    - Loyalty points updates (with/without free book redemption).
    - Validation failures (stock mismatch, loyalty mismatch, price mismatch).

 (See the test matrix below:)

| Scenario | Method | Expected result |
|:---------|:-------|:----------------|
| Preview purchase successfully | `preview()` | Return correct total price, no errors |
| Preview calculates discount | `preview()` | Discount applied if eligible |
| Preview calculates loyalty + discount | `preview()` | Loyalty points calculated, free book applied if eligible |
| Preview fails if book out of stock | `preview()` | Throws `PurchaseInvalidException` |
| Preview fails if book not found | `preview()` | Throws `PurchaseInvalidException` |
| Purchase succeeds if conditions match (normal) | `purchase()` | Stock reduced, loyalty points updated |
| Purchase succeeds if conditions match (free book applied) | `purchase()` | Loyalty points reset to 0 after reward |
| Purchase fails if agreed price mismatch | `purchase()` | Throws `PurchaseInvalidException` |
| Purchase fails if loyalty reward mismatch | `purchase()` | Throws `PurchaseInvalidException` |
| Purchase fails if books unavailable | `purchase()` | Throws `PurchaseInvalidException` |

---

## Known Limitations and TODOs

- **Single Book Instances**:  
  Only one copy of each book can be purchased per transaction (no quantity field).

- **Pagination Missing**:  
  `GET /books` API is not paginated yet.

- **Authentication and Authorization**:  
  APIs are open; authentication would be required for production.

---

## Potential Future Improvements

- Add Shopping Cart functionality with expiration management.
- Introduce JWT-based authentication and role-based access control.
- Integrate a message broker (e.g., Kafka, RabbitMQ) to produce events for external systems (e.g., Data Warehouses, Reporting engines).
- Add admin APIs for managing inventory lifecycle and customer accounts.
- Extend to cloud deployment (AWS/GCP) with container orchestration.
- localization of book titles, error messages, etc.
---


