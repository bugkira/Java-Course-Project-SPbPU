# Java_spbstu
Репозиторий для публикации решений домашних задач по дисциплине "Приёмы программирования на языке Java" студента гр. 5130203/20102 Даниила Середы

## Step 2: Unit Tests

Добавлены unit-тесты для всех компонентов приложения:

### Service Layer Tests
- **TaskManagementServiceTest** - тестирование бизнес-логики управления задачами
- **UserManagementServiceTest** - тестирование логики пользователей и валидации
- **NotificationManagementServiceTest** - тестирование системы уведомлений

### Repository Layer Tests
- **MemoryTaskRepositoryTest** - тестирование in-memory хранилища задач
- **MemoryUserRepositoryTest** - тестирование in-memory хранилища пользователей
- **MemoryNotificationRepositoryTest** - тестирование in-memory хранилища уведомлений

### Domain Model Tests
- **TaskEntityTest** - тестирование модели задач (Builder pattern, equals/hashCode)
- **UserEntityTest** - тестирование модели пользователей
- **NotificationEntityTest** - тестирование модели уведомлений

### Exception Tests
- **EntityNotFoundExceptionTest** - тестирование исключений "сущность не найдена"
- **EntityAlreadyExistsExceptionTest** - тестирование исключений "сущность уже существует"

