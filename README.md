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

