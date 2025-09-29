# Java_spbstu
Репозиторий для публикации решений домашних задач по дисциплине "Приёмы программирования на языке Java" студента гр. 5130203/20102 Даниила Середы

##  Реализованные Steps

<details>
<summary><b>Step 1: Basic REST API with In-Memory Storage</b></summary>

###  Цель
Создание базового REST API с хранением данных в памяти

###  Реализовано
- **REST Controllers**: TaskApiController, UserApiController, NotificationApiController
- **Service Layer**: TaskManagementService, UserManagementService, NotificationManagementService
- **In-Memory Storage**: MemoryTaskRepository, MemoryUserRepository, MemoryNotificationRepository
- **Domain Models**: TaskEntity, UserEntity, NotificationEntity с Lombok
- **Exception Handling**: EntityNotFoundException, EntityAlreadyExistsException
- **HTTP Endpoints**: GET, POST, DELETE с правильными статус-кодами

###  Технологии
- Spring Boot 3.4.5
- Lombok для упрощения кода
- HashMap для хранения данных
- REST API архитектура

</details>

<details>
<summary><b>Step 2: Write unit-tests</b></summary>

### Цель
Покрытие всего приложения unit-тестами

###  Реализовано
**unit-тесты** для всех компонентов:

#### Service Layer Tests
- **TaskManagementServiceTest** - тестирование бизнес-логики управления задачами
- **UserManagementServiceTest** - тестирование логики пользователей и валидации
- **NotificationManagementServiceTest** - тестирование системы уведомлений

#### Repository Layer Tests
- **MemoryTaskRepositoryTest** - тестирование in-memory хранилища задач
- **MemoryUserRepositoryTest** - тестирование in-memory хранилища пользователей
- **MemoryNotificationRepositoryTest** - тестирование in-memory хранилища уведомлений

#### Domain Model Tests
- **TaskEntityTest** - тестирование модели задач (Builder pattern, equals/hashCode)
- **UserEntityTest** - тестирование модели пользователей
- **NotificationEntityTest** - тестирование модели уведомлений

#### Exception Tests
- **EntityNotFoundExceptionTest** - тестирование исключений "сущность не найдена"
- **EntityAlreadyExistsExceptionTest** - тестирование исключений "сущность уже существует"

###  Технологии
- JUnit 5
- Mockito для мокирования
- Spring Test Context
- AAA Pattern (Arrange-Act-Assert)

</details>

<details>
<summary><b>Step 3: In-Memory Database (H2)</b></summary>

###  Цель
Интеграция H2 database с Spring Data JPA

###  Реализовано
- **H2 Database**: In-memory база данных с консолью
- **Spring Data JPA**: Полная интеграция с JPA/Hibernate
- **Entity Mapping**: JPA аннотации (@Entity, @Id, @GeneratedValue, @Table)
- **JPA Repositories**: TaskJpaRepository, UserJpaRepository, NotificationJpaRepository
- **Profile Architecture**:
  - `inmemory` - для Step 1-2 (HashMap storage)
  - `h2` - для Step 3 (JPA + H2 database)
- **Обратная совместимость**: Все предыдущие функции сохранены

###  Архитектура
- **JPA Entity классы**: TaskEntity, UserEntity, NotificationEntity
- **Repository интерфейсы**: TaskDataRepository, UserDataRepository, NotificationDataRepository
- **Memory реализации**: MemoryTaskRepository, MemoryUserRepository, MemoryNotificationRepository
- **JPA реализации**: JpaTaskRepository, JpaUserRepository, JpaNotificationRepository

###  Технологии
- Spring Data JPA
- H2 Database
- Hibernate ORM
- Spring Profiles
- JPA/Hibernate аннотации

###  Результат
- **36 Java файлов** (включая тесты)
- **11 тестовых файлов**
- **Все тесты проходят** ✅
- **H2 консоль доступна** по адресу `/h2-console`

</details>

<details>
<summary><b>Step 4: Docker Support</b></summary>

### Цель
Добавить поддержку Docker для контейнеризации приложения с автоматизированным тестированием

### Реализованные функции
- **Dockerfile**: многоступенчатая сборка Spring Boot приложения
- **docker-compose.yml**: упрощенная оркестрация (только приложение с H2)
- **Профиль docker**: конфигурация для контейнеризованного окружения
- **Интеграционные тесты**: автоматизация тестирования API
- **Замена curl команд**: полноценные автотесты вместо ручного тестирования

### Архитектура
- **Dockerfile**:
  - Stage 1: Gradle сборка (gradle:jdk17)
  - Stage 2: Runtime образ (eclipse-temurin:17-jre-jammy)
- **application-docker.properties**: H2 база данных для Docker окружения
- **Интеграционные тесты**:
  - `BasicIntegrationTest` - основные API тесты
  - `SimpleIntegrationTest` - простые сценарии
  - `DockerIntegrationTest` - тесты для Docker профиля
  - `H2ProfileIntegrationTest` - тесты для H2 профиля
  - `EndToEndIntegrationTest` - полные end-to-end сценарии

### Технологии
- Docker & Docker Compose
- H2 Database (in-memory для Step 4)
- Spring Boot в контейнере
- TestRestTemplate для интеграционных тестов
- Spring Test Context

### Результат
- **Контейнеризованное приложение**
- **H2 база данных в Docker**
- **API работает в контейнерах**
- **Автоматизированные интеграционные тесты**
- **Замена ручных curl команд на автотесты**
- **Многоэтапная Docker сборка**

</details>



<details>
<summary><b>Step 5: Switch to PostgreSQL Database</b></summary>

### Цель
Заменить H2 на PostgreSQL с использованием Flyway для миграций и Mockito для тестирования

### Реализованные функции
- **PostgreSQL интеграция**: полная замена H2 на PostgreSQL
- **Flyway миграции**: автоматическое управление схемой базы данных
- **Mockito тестирование**: мокирование репозиториев для unit-тестов
- **Профиль postgresql**: отдельная конфигурация для PostgreSQL
- **Docker Compose**: обновленная оркестрация с PostgreSQL контейнером

### Архитектура
- **application-postgresql.properties**: конфигурация PostgreSQL подключения
- **Flyway миграции**:
  - V1__Create_users_table.sql
  - V2__Create_tasks_table.sql
  - V3__Create_notifications_table.sql
- **Mockito тесты**:
  - `PostgreSQLServiceTest` - тестирование сервисов с мокированием
  - `PostgreSQLRepositoryTest` - тестирование репозиториев
  - `PostgreSQLIntegrationTest` - интеграционные тесты для PostgreSQL профиля
- **Docker Compose**: PostgreSQL + Spring Boot приложение с health checks

### Технологии
- PostgreSQL 15 Alpine
- Flyway для миграций базы данных
- Mockito для unit-тестирования
- Spring Data JPA с PostgreSQL
- Docker Compose с health checks

### Результат
- **PostgreSQL база данных** 
- **Flyway миграции** 
- **Mockito тестирование** 
- **Все профили работают** (inmemory, h2, docker, postgresql) 
- **Docker Compose с PostgreSQL** 
- **Автоматическое управление схемой** 

</details>

## Отчет о реализации Step 5

### Выполненные задачи

**1. PostgreSQL интеграция**
- Полная замена H2 на PostgreSQL в качестве основной базы данных
- Настройка подключения через Spring Data JPA с PostgreSQL драйвером
- Создание профилей `postgresql` и обновление `docker` профиля для PostgreSQL
- Конфигурация connection pool с HikariCP

**2. Flyway миграции**
- Добавление зависимости `flyway-database-postgresql` для поддержки PostgreSQL
- Создание трех миграций:
  - V1__Create_users_table.sql - таблица пользователей
  - V2__Create_tasks_table.sql - таблица задач с внешними ключами
  - V3__Create_notifications_table.sql - таблица уведомлений
- Настройка автоматического выполнения миграций при запуске приложения

**3. Автоматизированное тестирование**
- Интеграция Docker Compose плагина для Gradle
- Автоматический запуск PostgreSQL контейнера перед тестами
- Автоматическая остановка и очистка контейнеров после тестов
- Health checks для ожидания готовности PostgreSQL

**4. HTTP коды ошибок**
- Исправление статус кодов согласно REST API стандартам:
  - 401 Unauthorized для неверных логинов/паролей
  - 409 Conflict для дублирующихся сущностей
  - 400 Bad Request для валидационных ошибок
  - 500 Internal Server Error для неожиданных ошибок
- Создание `DuplicateEntityException` для более точной обработки ошибок

**5. Mockito тестирование**
- Создание `MockitoRepositoryIntegrationTest` для демонстрации мокирования репозиториев
- Обновление существующих unit тестов для работы с новыми исключениями
- Тестирование сервисного слоя с мокированными репозиториями

**6. Интеграционные тесты**
- Исправление всех интеграционных тестов для работы с PostgreSQL
- Корректировка эндпоинтов согласно реальной API структуре
- Использование уникальных данных в тестах для избежания конфликтов
- Создание `PostgreSQLProfileIntegrationTest` для специфичного тестирования PostgreSQL

### Технические улучшения

**Архитектура**
- Сохранение всех предыдущих профилей (inmemory, h2, docker, postgresql)
- Обратная совместимость с предыдущими шагами
- Четкое разделение конфигураций по профилям

**Тестирование**
- 177 тестов проходят успешно
- Автоматическое управление жизненным циклом PostgreSQL контейнера
- Изоляция тестов через уникальные данные
- Полное покрытие всех компонентов системы

**DevOps**
- Автоматизированная сборка и тестирование
- Docker Compose для локальной разработки
- Health checks для надежности развертывания
- Автоматическая очистка ресурсов после тестов

### Результат

Step 5 полностью соответствует техническому заданию:
- PostgreSQL база данных интегрирована
- Flyway миграции настроены и работают
- Mockito тестирование реализовано
- HTTP коды ошибок исправлены
- Автоматизированное тестирование с Docker Compose
- Все требования выполнены без забегания вперед
