# Java Course Project - SPbPU

## Описание
Курсовая работа по дисциплине "Приёмы программирования на языке Java" - поэтапная разработка веб-сервиса управления задачами.

## Технологии
- Java 23
- Spring Boot 3.4.5
- Spring Data JPA
- PostgreSQL / H2
- Redis
- RabbitMQ
- Docker
- JUnit 5

## Структура проекта
Каждый этап разработки находится в отдельной ветке:
- `step1` - Базовая REST API с in-memory хранилищем
- `step2` - Unit тесты
- `step3` - H2 In-Memory Database
- `step4` - Docker поддержка
- `step5` - PostgreSQL с Flyway
- `step6` - Redis кэширование
- `step7` - RabbitMQ messaging
- `step8` - Scheduling & Async Tasks

## Запуск
```bash
# In-memory storage
./gradlew bootRun -Dspring.profiles.active=inmemory

# H2 Database
./gradlew bootRun -Dspring.profiles.active=h2

# PostgreSQL
./gradlew bootRun -Dspring.profiles.active=postgres
```

## API Documentation
Swagger UI доступен по адресу: http://localhost:8080/swagger-ui.html

## Автор
Студент группы 5130203/20102 Середа Даниил