# Java home assignment

<!-- TOC -->

* [Java home assignment](#java-home-assignment)
    * [How to launch](#how-to-launch)
        * [Prerequisites](#prerequisites)
        * [Steps](#steps)
    * [Project structure](#project-structure)
    * [Dependencies](#dependencies)
    * [Testing](#testing)
    * [What could be improved](#what-could-be-improved)

<!-- TOC -->

## How to launch

### Prerequisites

- Java Runtime (JRE) on system to run Gradle
    - Project uses JDK 21, but you do not need to install it. Gradle Toolchains will do that for you.
- Docker
    - Will obtain postgres DB image to run Liquibase migrations and app transactions

### Steps

1) Clone repo

```bash
git clone https://github.com/romansj/hwk
```

<br>

2) Launch the [app](/src/main/java/com/jromans/hwk/HwkApplication.java)
   Connection to real FX service

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

Mocked FX service

```bash
./gradlew bootRun
```

<br>
<br>

3) Open Swagger UI http://localhost:8080/swagger-ui/index.html

For convenience test data is loaded in migration script (loaded with the default profile and prod, but not itest).
For example, you could transfer money between these 2 accounts:

```csv
1, LV10MINT0012345678910, EUR, 5000.25
2, LV10MINT0012345678911, EUR, 2000
```

## Project structure

```
-- accounts (account data, list of accounts)
-- customers (customer data, list of customers)
-- fx (currency conversion, real service and fake)
-- transactions (money transfers, transaction history, balance updates)

and:
-- shared
  -- constants (reused constants across several packages)
-- configuration (mapping configuration and exception handling)
```

## Dependencies

Latest as of 2023-11-03

### Project core dependencies

- JDK 21
- Spring Boot 3.1.5
    - Using 3.2.0 Release Candidate 2 to avoid Docker Compose issue
- Gradle 8.4

### Others

- Lombok  
  Private constructor could be created in less lines without Lombok, but I used Lombok throughout for consistency.
- jacoco  
  To get a coverage report to compare with Intellij's "Run tests with coverage" option
- Testcontainers  
  Throwaway DB instances for integration tests
- Spring Boot Starters  
  Out of the box functionality, e.g. API doc generation, validation

### Dependency management

Not using Spring Dependency Management plugin.  
Using Gradle Version Catalogs for faster dependency resolution during build/sync. Spring Boot managed (compatible) dependency versions still used -- imported as BOM.

Some dependency versions are overriden with newer versions.

## Testing

Full project coverage reported by IntelliJ

- Class 90%
- Method 79%
- Line 86%

Full project coverage reported by Jacoco
_(Excluding lombok generated code from Jacoco coverage report in lombok.config https://stackoverflow.com/a/51840807/4673960)_

- Instructions 82%
- Branches 71%

<br>

Notes:

- IJ coverage: FxRateServiceImplTest needs to be enabled for more accurate result
- Lowest coverage score is for configuration->exceptions and mapping (generated code)

## What could be improved

- Consolidate validation. Here I've used multiple ways (@Validated in basic Controller params, heavier validation in service, own constraint definitions), but it would be nice to consolidate this.
- Much simpler and clearer TransactionService. I didn't spend as much time as I'd have liked to plan and design the service class.
    - Splitting requests from completed transactions, would help decouple the two concepts and simplify the service. In a more realistic application, request creation would be completely separate and messaging (e.g.
      RabbitMQ) would be a good tool here.
- Split tests into unit tests and integration tests
- Fx
    - Not saving every rate, but only if some period is different, e.g. hour changed. This way there is no reliance for historical data on the 3rd party either.
    - Caching before requesting / DB access:
        - Store rate and date. If service not available, return the last available rate. Store used rate + rate date. Once service is available, rebate customers who were charged more than the actual historical rate.
        - Invert (1/rate) to get vice versa rate when filling fx cache
- Testing
    - Simplifying Data.java itself, which is meant to simplify getting correct test data.
