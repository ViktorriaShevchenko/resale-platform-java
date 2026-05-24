# Resale Platform

[![Java CI](https://github.com/ViktorriaShevchenko/resale-platform-java/actions/workflows/java-ci.yml/badge.svg)](https://github.com/ViktorriaShevchenko/resale-platform-java/actions/workflows/java-ci.yml)


🇷🇺 [Русская версия](./README.ru.md)

Backend REST API for a resale marketplace (ads + comments + auth).  
Built with Java, Spring Boot, PostgreSQL, Spring Security, Liquibase.

---
## About

This is the backend part of an ad posting platform (similar to a marketplace).  
It provides a REST API for user management, ads, comments, and images with authentication and role-based access control.

---

## Features

- User registration and authentication (JWT)
- Profile management
- Create, update, and delete ads
- Paginated and sorted ad listings via dedicated endpoint
- Image upload and storage
- Comments on ads
- Role-based access (USER / ADMIN)
- OpenAPI / Swagger documentation

---

## Tech Stack

- Java 11
- Spring Boot 2.7.15
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- H2 (for tests)
- Liquibase (database migrations)
- MapStruct (DTO mapping)
- Lombok
- Springdoc OpenAPI
- Maven
- Docker

---

## Architecture

Layered architecture (Controller → Service → Repository):

- **Controller** – HTTP request handling
- **Service** – business logic
- **Repository** – database access
- **DTO / Mapper** – data transformation

---

## Getting Started

### Prerequisites

- Java 11
- Maven
- PostgreSQL 12+

### Create Database

```sql
CREATE DATABASE resale_platform_db;
```

### Configuration

Update application.properties with your database credentials:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/resale_platform_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```
### Build & Run
```
mvn clean install
mvn spring-boot:run
```
### After startup
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html

---

## Frontend for Local Testing

A Docker image is available for the frontend:
```
docker run -p 3000:3000 --rm ghcr.io/dmitry-bizin/front-react-avito:v1.21
```

Then open: `http://localhost:3000`

---

## Testing

```
mvn test
```

Includes:

- Unit tests
- Integration tests
- Security tests
- H2 in-memory database for test environment

---

## API Documentation

Swagger UI is available after running the application:

http://localhost:8080/swagger-ui.html

---

## Possible Improvements
- Search and filtering
- Docker Compose (backend + database + frontend)

---

## Author 

Viktorria Shevchenko

GitHub: [@ViktorriaShevchenko](https://github.com/ViktorriaShevchenko)