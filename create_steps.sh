#!/bin/bash

# Step 1: Basic REST API with In-Memory Storage
echo "Creating Step 1: Basic REST API with In-Memory Storage"
git checkout step1

# Remove all advanced features, keep only basic ones
rm -rf src/main/java/spbstu/TasksApplication/config/
rm -rf src/main/java/spbstu/TasksApplication/messaging/
rm -rf src/main/java/spbstu/TasksApplication/service/impl/
rm -rf src/main/java/spbstu/TasksApplication/repository/impl/Jpa*
rm -rf src/main/resources/db/
rm -rf src/test/

# Update build.gradle for Step 1
cat > build.gradle << 'EOF'
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.4.5'
	id 'io.spring.dependency-management' version '1.1.7'
}

group = 'spbstu'
version = '0.0.1-SNAPSHOT'

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.projectlombok:lombok'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	annotationProcessor 'org.projectlombok:lombok'
	
	// Swagger UI
	implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
	
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

tasks.named('test') {
	useJUnitPlatform()
}
EOF

# Update application.properties for Step 1
cat > src/main/resources/application.properties << 'EOF'
spring.application.name=tasks
spring.profiles.active=inmemory
EOF

# Create only inmemory profile
cat > src/main/resources/application-inmemory.properties << 'EOF'
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
EOF

# Remove other profiles
rm -f src/main/resources/application-h2.properties
rm -f src/main/resources/application-postgres.properties

# Remove Docker files
rm -f Dockerfile
rm -f docker-compose.yml

git add .
git commit -m "Step 1: Basic REST API with In-Memory Storage

- Implemented TaskController with CRUD operations
- Implemented UserController with registration and login  
- Implemented NotificationController for user notifications
- Added in-memory repositories for data storage
- Added proper error handling and validation
- Tasks have creation date and target date
- Soft delete implementation for tasks"

echo "Step 1 created successfully!"
