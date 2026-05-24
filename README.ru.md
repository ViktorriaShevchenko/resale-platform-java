# Resale Platform

Backend-приложение для платформы размещения объявлений (аналог marketplace), реализующее REST API для управления пользователями, объявлениями, комментариями и изображениями.

## О проекте

Проект разработан как backend-часть сервиса объявлений с поддержкой аутентификации, ролевой модели доступа и работы с медиа-контентом.

---

## Функциональность

- Регистрация и аутентификация пользователей
- Управление профилем пользователя
- Создание, редактирование и удаление объявлений
- Получение объявлений с пагинацией и сортировкой через отдельный endpoint
- Загрузка и хранение изображений
- Работа с комментариями
- Ролевая модель доступа (USER / ADMIN)
- Документация API (Swagger / OpenAPI)

---

## Технологии

- Java 11
- Spring Boot 2.7.15
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- H2 (для тестов)
- Liquibase (миграции БД)
- MapStruct (маппинг DTO)
- Lombok
- Springdoc OpenAPI
- Maven
- Docker

---

## Архитектура

Проект построен по классической многослойной архитектуре:

- **Controller** — обработка HTTP-запросов
- **Service** — бизнес-логика
- **Repository** — работа с базой данных
- **DTO / Mapper** — преобразование данных между слоями

---

## Запуск проекта

### Требования

- Java 11
- Maven
- PostgreSQL 12+

### Создание базы данных

```sql
CREATE DATABASE resale_platform_db;
```
### Конфигурация

Укажите параметры подключения к БД в `application.properties`:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/resale_platform_db
spring.datasource.username=ваш_логин
spring.datasource.password=ваш_пароль
```
### Сборка и запуск
```
mvn clean install
mvn spring-boot:run
```
### После запуска приложение доступно:
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html

---

## Frontend

Frontend предоставляется в виде Docker-образа для локального тестирования.

Запуск:
```
docker run -p 3000:3000 --rm ghcr.io/dmitry-bizin/front-react-avito:v1.21
```

После запуска доступен по адресу:

`http://localhost:3000`

---

## Тестирование

Для запуска тестов:

```
mvn test
```

В проекте используются:

- unit-тесты
- интеграционные тесты
- тесты безопасности
- H2 база данных для тестового окружения

---

## Документация API

Swagger доступен после запуска:

http://localhost:8080/swagger-ui.html

---

## Возможные улучшения
- Реализация поиска и фильтрации
- Настройка Docker Compose (backend + БД + frontend)
- Добавление CI/CD пайплайна

---

## Автор 

Шевченко Виктория

Github: https://github.com/ViktorriaShevchenko