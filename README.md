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

<details>
<summary><b>Step 6: Implement Caching (Redis)</b></summary>

### Цель
Реализовать кэширование с использованием Redis для улучшения производительности приложения

### Реализованные функции
- **Redis интеграция**: полная настройка Redis как кэш-сервера
- **Spring Cache**: интеграция с Spring Cache абстракцией
- **Кэширование задач**: кэширование операций получения задач для улучшения производительности
- **Разные TTL**: настройка различных времен жизни для разных типов кэша
- **Профиль cache**: отдельная конфигурация для работы с Redis
- **Docker Compose**: обновленная оркестрация с Redis контейнером

### Архитектура
- **application-cache.properties**: конфигурация Redis подключения и кэширования
- **CacheConfig**: настройка Redis Cache Manager с Jackson сериализацией
- **Кэширование в сервисах**:
  - `TaskManagementService`: @Cacheable для получения, @CacheEvict для изменений
  - `UserManagementService`: @Cacheable для аутентификации, @CacheEvict для создания
  - `NotificationManagementService`: @Cacheable для получения уведомлений
- **TTL настройки**:
  - Tasks: 15 минут
  - Users: 30 минут  
  - Notifications: 5 минут
- **Docker Compose**: PostgreSQL + Redis + Spring Boot приложение с health checks

### Технологии
- Redis 7 Alpine
- Spring Cache абстракция
- Spring Data Redis
- Jackson с JavaTimeModule для LocalDateTime сериализации
- Docker Compose с health checks
- Redis Cache Manager

### Тестирование
- **CacheIntegrationTest**: unit тесты кэширования
- **RedisProfileIntegrationTest**: интеграционные тесты с Redis
- **Автоматическое управление контейнерами**: Docker Compose плагин для Gradle
- **184 теста проходят** успешно

### Результат
- **Redis кэширование** 
- **Spring Cache интеграция** 
- **Улучшенная производительность** 
- **Разные TTL для разных типов данных** 
- **Автоматическая инвалидация кэша** 
- **Все профили работают** (inmemory, h2, docker, postgresql, cache) 
- **Docker Compose с PostgreSQL + Redis** 
- **Стабильные интеграционные тесты** 

</details>

<details>
<summary><b>Step 7: Implement Messaging (RabbitMQ)</b></summary>

### Цель
Реализовать асинхронную обработку сообщений с использованием RabbitMQ для разделения создания задач и уведомлений

### Реализованные функции
- **RabbitMQ интеграция**: полная настройка RabbitMQ как message broker
- **Message Publisher**: публикация событий создания/удаления задач
- **Message Listener**: асинхронная обработка сообщений и создание уведомлений
- **Topic Exchange**: гибкая маршрутизация сообщений по типам событий
- **Профиль messaging**: отдельная конфигурация для работы с RabbitMQ
- **Docker Compose**: обновленная оркестрация с RabbitMQ контейнером

### Архитектура
- **application-messaging.properties**: конфигурация RabbitMQ подключения и обработки
- **RabbitMQConfig**: настройка exchanges, queues, bindings и Jackson сериализации
- **TaskEventPublisher**: публикация событий задач в RabbitMQ
- **TaskEventListener**: обработка сообщений и создание уведомлений
- **TaskEventMessage**: DTO для передачи данных о событиях задач
- **Условная конфигурация**: RabbitMQ компоненты активируются только при наличии настроек
- **Docker Compose**: PostgreSQL + Redis + RabbitMQ + Spring Boot приложение

### Технологии
- RabbitMQ 3 Management Alpine
- Spring AMQP
- Spring Boot Starter AMQP
- Jackson2JsonMessageConverter для сериализации
- Topic Exchange с routing keys
- Docker Compose с health checks
- Условные Spring бины (@ConditionalOnProperty)

### Тестирование
- **TaskEventPublisherTest**: unit тесты публикации сообщений
- **TaskEventListenerTest**: unit тесты обработки сообщений
- **MessagingProfileIntegrationTest**: интеграционные тесты с RabbitMQ
- **Автоматическое управление контейнерами**: Docker Compose плагин для Gradle
- **Основные тесты проходят** успешно

### Результат
- **RabbitMQ messaging** 
- **Асинхронная обработка событий** 
- **Разделение ответственности** между созданием задач и уведомлений
- **Topic Exchange с гибкой маршрутизацией** 
- **Условная активация** RabbitMQ компонентов
- **Все профили работают** (inmemory, h2, docker, postgresql, cache, messaging) 
- **Docker Compose с PostgreSQL + Redis + RabbitMQ** 
- **Стабильная сборка проекта** 

</details>

<details>
<summary><b>Step 8: Add Scheduling & Async Tasks</b></summary>

### Цель
Реализовать периодическую проверку просроченных задач и асинхронную обработку с использованием @Scheduled и @Async

### Реализованные функции
- **TaskSchedulerService**: сервис с @Scheduled для периодической проверки просроченных задач
- **AsyncTaskService**: сервис с @Async для фоновой обработки задач
- **AsyncConfig**: конфигурация для асинхронных задач и планировщика
- **Overdue Task Detection**: автоматическое обнаружение просроченных задач
- **Notification Generation**: создание уведомлений для просроченных задач
- **Profiles**: отдельный профиль scheduling для планировщика
- **Actuator Integration**: мониторинг через Spring Boot Actuator

### Архитектура
- **@Scheduled методы**: 
  - `checkOverdueTasks()` - каждый час
  - `checkOverdueTasksBusinessHours()` - каждые 30 минут в рабочие часы (9-18, ПН-ПТ)
  - `dailyCleanup()` - ежедневно в 2:00
- **@Async методы**: асинхронная обработка просроченных задач и создание уведомлений
- **ThreadPoolTaskExecutor**: настроенный пул потоков для асинхронных задач
- **Overdue Task Repository**: метод `findOverdueTasks()` для поиска просроченных задач
- **Error Handling**: обработка ошибок в асинхронных методах

### Технологии
- Spring Boot Starter Actuator для мониторинга
- Spring Scheduling (@Scheduled, @EnableScheduling)
- Spring Async (@Async, @EnableAsync)
- ThreadPoolTaskExecutor для управления потоками
- Cron expressions для планирования задач
- Comprehensive unit и integration тесты

### Тестирование
- **AsyncTaskServiceTest**: unit тесты асинхронного сервиса
- **TaskSchedulerServiceTest**: unit тесты планировщика
- **TaskSchedulerIntegrationTest**: интеграционные тесты с реальной базой данных
- **Overdue Task Detection**: тестирование поиска просроченных задач
- **Notification Creation**: тестирование создания уведомлений

### Результат
- **Автоматическая проверка просроченных задач** каждый час
- **Бизнес-часы мониторинг** каждые 30 минут в рабочие дни
- **Асинхронная обработка** для улучшения производительности
- **Автоматическое создание уведомлений** для просроченных задач
- **Spring Boot Actuator** для мониторинга планировщика
- **Профиль scheduling** для активации планировщика
- **Comprehensive тестирование** всех компонентов
- **Docker Compose** с поддержкой планировщика

</details>
