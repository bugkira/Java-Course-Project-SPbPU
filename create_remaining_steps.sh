#!/bin/bash

# Step 4: Docker Support
echo "Creating Step 4: Docker Support"
git checkout -b step4

# Add Docker files
cat > Dockerfile << 'EOF'
FROM gradle:8.13-jdk23 AS build
WORKDIR /workspace

COPY build.gradle settings.gradle gradlew gradle/ ./

RUN gradle --no-daemon dependencies

COPY . .

RUN gradle --no-daemon clean bootJar -x test

FROM eclipse-temurin:23-jdk-jammy AS runtime
WORKDIR /app

COPY --from=build  /workspace/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

cat > docker-compose.yml << 'EOF'
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=h2
EOF

git add .
git commit -m "Step 4: Added Docker support with Dockerfile and docker-compose"

# Step 5: PostgreSQL
echo "Creating Step 5: PostgreSQL"
git checkout -b step5

# Add PostgreSQL dependencies
sed -i '/H2 Database/a\\t// PostgreSQL Database\n\truntimeOnly '\''org.postgresql:postgresql'\''\n\n\t// Flyway Migration\n\timplementation '\''org.flywaydb:flyway-core'\''' build.gradle

# Add PostgreSQL properties
cat > src/main/resources/application-postgres.properties << 'EOF'
spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
spring.flyway.schemas=public
EOF

# Add Flyway migration
mkdir -p src/main/resources/db/migration
cat > src/main/resources/db/migration/V1__init_schema.sql << 'EOF'
CREATE TABLE IF NOT EXISTS users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS tasks (
    task_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    target_date TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS notifications (
    notification_id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    task_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (task_id) REFERENCES tasks(task_id)
);
EOF

git add .
git commit -m "Step 5: Added PostgreSQL database with Flyway migrations"

# Step 6: Redis Caching
echo "Creating Step 6: Redis Caching"
git checkout -b step6

# Add Redis dependency
sed -i '/Flyway Migration/a\\t// Redis Cache\n\timplementation '\''org.springframework.boot:spring-boot-starter-data-redis'\''' build.gradle

# Add Redis config
mkdir -p src/main/java/spbstu/TasksApplication/config
cat > src/main/java/spbstu/TasksApplication/config/RedisConfig.java << 'EOF'
package spbstu.TasksApplication.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withCacheConfiguration("tasks",
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("users",
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration("notifications",
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)))
                .build();
    }
}
EOF

git add .
git commit -m "Step 6: Added Redis caching support"

# Step 7: RabbitMQ Messaging
echo "Creating Step 7: RabbitMQ Messaging"
git checkout -b step7

# Add RabbitMQ dependency
sed -i '/Redis Cache/a\\t// RabbitMQ Messaging\n\timplementation '\''org.springframework.boot:spring-boot-starter-amqp'\''' build.gradle

# Add RabbitMQ config
cat > src/main/java/spbstu/TasksApplication/config/RabbitMQConfig.java << 'EOF'
package spbstu.TasksApplication.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TASK_CREATED_EXCHANGE = "task.created.exchange";
    public static final String TASK_CREATED_QUEUE = "task.created.queue";
    public static final String TASK_CREATED_ROUTING_KEY = "task.created";

    @Bean
    public Queue taskCreatedQueue() {
        return new Queue(TASK_CREATED_QUEUE);
    }

    @Bean
    public DirectExchange taskCreatedExchange() {
        return new DirectExchange(TASK_CREATED_EXCHANGE);
    }

    @Bean
    public Binding taskCreatedBinding() {
        return BindingBuilder
                .bind(taskCreatedQueue())
                .to(taskCreatedExchange())
                .with(TASK_CREATED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
EOF

# Add messaging classes
mkdir -p src/main/java/spbstu/TasksApplication/messaging
cat > src/main/java/spbstu/TasksApplication/messaging/TaskCreatedMessage.java << 'EOF'
package spbstu.TasksApplication.messaging;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TaskCreatedMessage {
    private Long taskId;
    private String title;
    private String description;
    private Long userId;
    private LocalDateTime creationDate;
    private LocalDateTime targetDate;
}
EOF

cat > src/main/java/spbstu/TasksApplication/messaging/TaskMessagePublisher.java << 'EOF'
package spbstu.TasksApplication.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import spbstu.TasksApplication.config.RabbitMQConfig;
import spbstu.TasksApplication.model.Task;

@Component
@RequiredArgsConstructor
public class TaskMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishTaskCreated(Task task) {
        TaskCreatedMessage message = TaskCreatedMessage.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .userId(task.getUserId())
                .creationDate(task.getCreationDate())
                .targetDate(task.getTargetDate())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TASK_CREATED_EXCHANGE,
                RabbitMQConfig.TASK_CREATED_ROUTING_KEY,
                message
        );
    }
}
EOF

cat > src/main/java/spbstu/TasksApplication/messaging/TaskMessageListener.java << 'EOF'
package spbstu.TasksApplication.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import spbstu.TasksApplication.config.RabbitMQConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskMessageListener {

    @RabbitListener(queues = RabbitMQConfig.TASK_CREATED_QUEUE)
    public void handleTaskCreated(TaskCreatedMessage message) {
        log.info("Received task created message: {}", message);
        // Process task creation event
    }
}
EOF

git add .
git commit -m "Step 7: Added RabbitMQ messaging support"

# Step 8: Scheduling & Async
echo "Creating Step 8: Scheduling & Async"
git checkout -b step8

# Add scheduler config
cat > src/main/java/spbstu/TasksApplication/config/SchedulerConfig.java << 'EOF'
package spbstu.TasksApplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulerConfig {
}
EOF

# Add scheduler service
mkdir -p src/main/java/spbstu/TasksApplication/service/impl
cat > src/main/java/spbstu/TasksApplication/service/impl/TaskSchedulerServiceImpl.java << 'EOF'
package spbstu.TasksApplication.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.repository.TaskRepository;
import spbstu.TasksApplication.service.TaskSchedulerService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskSchedulerServiceImpl implements TaskSchedulerService {

    private final TaskRepository taskRepository;

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkOverdueTasks() {
        log.info("Checking for overdue tasks...");
        List<Task> overdueTasks = findOverdueTasks();
        
        for (Task task : overdueTasks) {
            processOverdueTask(task);
        }
        
        log.info("Found {} overdue tasks", overdueTasks.size());
    }

    @Override
    @Async
    public void processOverdueTask(Task task) {
        log.info("Processing overdue task: {} for user: {}", task.getTitle(), task.getUserId());
        
        // Log overdue task for monitoring
        log.warn("Task '{}' is overdue! Target date: {}", task.getTitle(), task.getTargetDate());
    }

    @Override
    public List<Task> findOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        return taskRepository.findByUserIdAndIsCompletedFalseAndIsDeletedFalse(null)
                .stream()
                .filter(task -> task.getTargetDate().isBefore(now))
                .toList();
    }
}
EOF

git add .
git commit -m "Step 8: Added scheduling and async processing"

echo "All steps created successfully!"
