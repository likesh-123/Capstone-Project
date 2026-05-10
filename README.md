# E-Commerce Microservices Platform

A comprehensive Java-based microservices architecture for an e-commerce platform featuring user authentication, product management, payment processing, and email notifications.

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Microservices](#microservices)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Project Setup](#project-setup)
- [Running the Services](#running-the-services)
- [API Documentation](#api-documentation)
- [Database Configuration](#database-configuration)
- [Configuration Details](#configuration-details)
- [Service Communication](#service-communication)
- [Troubleshooting](#troubleshooting)

## Project Overview

This project implements a complete e-commerce platform using a microservices architecture pattern. It demonstrates key architectural principles including:

- **Service Independence**: Each microservice handles a specific business capability
- **Asynchronous Communication**: Kafka for event-driven messaging
- **Service Discovery**: Netflix Eureka for dynamic service registration
- **Database Per Service**: Separate databases for scalability and independence
- **Token-Based Security**: JWT-like token system for authentication
- **Third-Party Integration**: Razorpay payment gateway integration
- **Caching Strategy**: Redis for performance optimization

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway / Client                   │
└──────────┬──────────────┬──────────────┬────────────────────┘
           │              │              │
    ┌──────▼─────┐ ┌──────▼──────┐ ┌───▼───────┐
    │   User     │ │   Product   │ │  Payment  │
    │  Service   │ │  Service    │ │ Service   │
    │  (Auth)    │ │  (Catalog)  │ │(Payments) │
    └────────────┘ └─────────────┘ └───────────┘
           │              │              │
           │              │              ├─────────────────┐
           │              │              │                 │
    ┌──────▼──────────────▼──────────────▼──────┐ ┌────────▼──────────┐
    │         Eureka Service Discovery          │ │  Razorpay Gateway │
    └──────────────────────────────────────────┘ └───────────────────┘
           │
    ┌──────▼──────────────────────┐
    │   Kafka Message Broker      │
    │   (Email Events)            │
    └─────────────────────────────┘
           │
           └─────────────────────┐
                                 │
                    ┌────────────▼──────────┐
                    │   Email Service      │
                    │  (Async Processor)   │
                    └──────────────────────┘

    ┌─────────────┐  ┌──────────────┐
    │   MySQL     │  │   Redis      │
    │ Databases   │  │   Cache      │
    └─────────────┘  └──────────────┘
```

## Microservices

### 1. **User Service** (Port: 9000)
**Purpose**: Handles user authentication, registration, and role-based access control.

**Key Features**:
- User registration with email validation
- Secure password hashing using BCrypt
- Token-based authentication (30-day expiry)
- Role-based access control (RBAC)
- Token validation endpoints

**Database**: `userserviceaprbatch` (MySQL)

**Key Endpoints**:
```
POST   /users/signup              - Register new user
POST   /users/login               - Authenticate and get token
POST   /users/validate/{token}    - Validate token and retrieve user info
```

**Technology Stack**:
- Spring Boot 3.3.0
- Spring Security with OAuth2
- Spring Data JPA
- MySQL
- BCrypt password encoding

---

### 2. **Product Service** (Port: configured via `SERVER_PORT`)
**Purpose**: Manages product catalog with CRUD operations, search, categorization, and caching.

**Key Features**:
- Full product CRUD operations
- Product categorization
- Full-text search with pagination
- Redis caching for performance
- Product inventory management
- Support for specialized products (VIP products)
- Service discovery via Eureka

**Database**: `productserviceaprbatch` (MySQL)

**Key Endpoints**:
```
GET    /products                  - Fetch all products
GET    /products/{id}             - Get product by ID
POST   /products                  - Create new product
PATCH  /products/{id}             - Update product
GET    /search                    - Search products by keyword with pagination
GET    /products/product/{id}     - Internal: Fetch product details
```

**Technology Stack**:
- Spring Boot 3.2.5
- Spring Data JPA
- Spring Data Redis
- MySQL
- Netflix Eureka Client
- HTTP Client 5 for inter-service communication

**Database Schema**:
```
Tables:
- Product (id, title, price, category_id, description, quantity, isDeleted, created_at)
- Category (id, title, created_at)
```

---

### 3. **Payment Service** (Port: 9000)
**Purpose**: Processes payments using Razorpay payment gateway and manages payment transactions.

**Key Features**:
- Integration with Razorpay payment gateway
- Payment link generation
- Webhook handling for payment confirmations
- Customer email and SMS notifications
- Partial payment support
- Service discovery via Eureka

**Key Endpoints**:
```
POST   /payments                  - Initiate payment (email, phone, amount, orderId)
POST   /payments/webhook          - Receive Razorpay webhook callbacks
GET    /payments/product/{id}     - Fetch product details (calls Product Service)
```

**Technology Stack**:
- Spring Boot 3.3.7
- Razorpay Java SDK 1.4.8
- Netflix Eureka Client
- HTTP Client for service communication

**Configuration**:
```
Razorpay Test Credentials:
- Key ID: rzp_test_DKhug6IIS5VFlG
- Key Secret: 1bkWQLelMv6e6Fjw5ynEIuHJ
```

---

### 4. **Email Service** (Port: default Spring Boot port unless overridden)
**Purpose**: Asynchronous email notification service that consumes events from Kafka and sends emails via SMTP.

**Key Features**:
- Event-driven architecture using Kafka
- SMTP integration with Gmail
- HTML email support
- Asynchronous processing
- Email event consumption from Kafka topic

**Key Endpoints**:
```
GET    /email/{emailId}           - Publish email event to Kafka topic "sendEmail"
```

**Technology Stack**:
- Spring Boot 3.3.1
- Spring Kafka
- JavaMail (SMTP)
- Async processing

**Kafka Configuration**:
- Topic: `sendEmail`
- Consumer Group: `emailService`

**Email Event DTO**:
```java
{
  "to": "recipient@example.com",
  "from": "sender@example.com",
  "subject": "Email Subject",
  "body": "HTML email body"
}
```

## Technologies

### Core Framework
- **Java 17** - Programming language
- **Spring Boot** 3.2.5 - 3.3.7 - Application framework
- **Maven** - Build tool

### Web & API
- **Spring Web** - REST API development
- **Spring REST** - RESTful web services

### Data Access & Persistence
- **Spring Data JPA** - Object-relational mapping
- **MySQL 8** - Relational database
- **Hibernate** - ORM framework
- **Spring Data Redis** - Cache management

### Security & Authentication
- **Spring Security** - Authentication and authorization
- **OAuth2 Authorization Server** - Token-based auth
- **BCrypt** - Password hashing

### Messaging & Events
- **Apache Kafka** - Message broker
- **Spring Kafka** - Kafka integration
- **Spring Cloud Stream** - Event streaming (potential)

### Microservices Infrastructure
- **Netflix Eureka Client** - Service discovery and registration

### Email & Communication
- **JavaMail (javax.mail)** - SMTP email sending
- **Razorpay SDK** - Payment gateway integration

### Utilities
- **Lombok** - Boilerplate code reduction
- **Apache Commons Lang3** - Utility functions
- **HTTP Client 5** - HTTP requests between services

## Prerequisites

### System Requirements
- **Java 17** or higher
- **Maven 3.6+** (or use included Maven wrapper)
- **MySQL 8.0+**
- **Redis** (for caching)
- **Apache Kafka** (for messaging)

### Software Installation

**1. Java 17 Setup**
```bash
# Verify installation
java -version

# Should output Java 17.x.x or higher
```

**2. MySQL Installation & Setup**
```bash
# Windows Installation via Chocolatey
choco install mysql-server

# Or download from: https://dev.mysql.com/downloads/mysql/

# Create databases
mysql -u root -p
```

```sql
-- User Service Database
CREATE DATABASE userserviceaprbatch;

-- Product Service Database
CREATE DATABASE productserviceaprbatch;
```

**3. Redis Installation**
```bash
# Windows (via Chocolatey)
choco install redis-64

# Or download: https://redis.io/download
```

**4. Kafka Installation**
```bash
# Download from: https://kafka.apache.org/downloads
# Extract and add to PATH

# Start Zookeeper (in one terminal)
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

# Start Kafka (in another terminal)
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

**5. Eureka Server Setup (Optional Service Discovery)**
```bash
# Can be run as a separate Spring Boot application
# Configure with: eureka.server.enable-self-preservation=false
```

## Project Setup

### Sub-repository GitHub URLs

All the microservice github url as below:

- User Service:

```bash
https://github.com/likesh-123/UserService
```

- Product Service:

```bash
https://github.com/likesh-123/ProductService
```

- Payment Service:

```bash
https://github.com/likesh-123/PaymentService
```

- Email Service:

```bash
https://github.com/likesh-123/EmailService
```



### 1. Clone and Navigate to Project
```bash
cd "d:\Videos\Java\Completing the Capstone Project"
```

### 2. Build All Services
```bash
# Build Email Service
cd emailservice
mvnw.cmd clean install
cd ..

# Build Payment Service
cd paymentservice
mvnw.cmd clean install
cd ..

# Build Product Service
cd productservice
mvnw.cmd clean install
cd ..

# Build User Service
cd userservice
mvnw.cmd clean install
cd ..
```

Or build all at once:
```bash
# From root directory
for /d %S in (*service) do (cd %S && mvnw.cmd clean install && cd ..)
```

### 3. Create MySQL Databases
```sql
CREATE DATABASE userserviceaprbatch;
CREATE DATABASE productserviceaprbatch;
```

### 4. Update Configuration Files
Each service has an `application.properties` file in `src/main/resources/`:

**userservice/src/main/resources/application.properties**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/userserviceaprbatch
spring.datasource.username=root
spring.datasource.password=password
```

**productservice/src/main/resources/application.properties**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/productserviceaprbatch
spring.datasource.username=root
spring.datasource.password=password
```

Update the MySQL username and password as needed for your setup.

## Running the Services

### Prerequisite: Start Infrastructure Services
**1. Start Redis**
```bash
redis-server
```

**2. Start Kafka**
```bash
# Terminal 1: Zookeeper
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

# Terminal 2: Kafka Broker
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

**3. (Optional) Start Eureka Server**
```bash
# If you have an Eureka server application
java -jar eureka-server.jar
```

### Start Microservices

**Start User Service (Port 9000)**
```bash
cd userservice
mvnw.cmd spring-boot:run
```

**Start Product Service (Set `SERVER_PORT` first, then run)**
```bash
cd productservice
set SERVER_PORT=8081
mvnw.cmd spring-boot:run
```

**Start Payment Service (Port 9001)**
```bash
cd paymentservice
mvnw.cmd spring-boot:run
```

**Start Email Service (Default Spring Boot port unless configured)**
```bash
cd emailservice
mvnw.cmd spring-boot:run
```

### Alternative: Run with IDE
- Import each service as a Maven project in IntelliJ IDEA
- Right-click project → Run → Run 'Application'

## API Documentation

### User Service API

**1. User Registration**
```bash
POST /users/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "name": "John Doe",
  "password": "securePassword123"
}

Response:
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "roles": ["USER"]
}
```

**2. User Login**
```bash
POST /users/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}

Response:
{
  "token": "abc123xyz789...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe"
  }
}
```

**3. Validate Token**
```bash
POST /users/validate/abc123xyz789
Authorization: Bearer abc123xyz789

Response:
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "isValid": true
}
```

### Product Service API

**1. Get All Products**
```bash
GET /products

Response:
[
  {
    "id": 1,
    "title": "Product 1",
    "price": 29.99,
    "category": "Electronics",
    "quantity": 100,
    "description": "Product description"
  }
]
```

**2. Get Product by ID**
```bash
GET /products/1

Response:
{
  "id": 1,
  "title": "Product 1",
  "price": 29.99,
  "category": "Electronics",
  "quantity": 100
}
```

**3. Create Product**
```bash
POST /products
Content-Type: application/json

{
  "title": "New Product",
  "price": 49.99,
  "categoryId": 1,
  "description": "New product description",
  "quantity": 50
}

Response: Created product object with ID
```

**4. Update Product**
```bash
PATCH /products/1
Content-Type: application/json

{
  "title": "Updated Product Name",
  "price": 59.99,
  "quantity": 75
}
```

**5. Search Products**
```bash
GET /search?keyword=laptop&page=0&size=10

Response:
[
  {
    "id": 1,
    "title": "Gaming Laptop",
    "price": 1299.99,
    "category": "Electronics"
  }
]
```

### Payment Service API

**1. Initiate Payment**
```bash
POST /payments
Content-Type: application/json

{
  "email": "customer@example.com",
  "phone": "9876543210",
  "amount": 5000.00,
  "orderId": "ORDER123"
}

Response:
{
  "paymentLinkId": "plink_abc123",
  "paymentLinkUrl": "https://rzp.io/...",
  "status": "created"
}
```

**2. Payment Webhook**
```bash
POST /payments/webhook
Content-Type: application/json

{
  "event": "payment_link.paid",
  "payload": {
    "payment_link": {
      "id": "plink_abc123",
      "reference_id": "ORDER123",
      "amount": 500000,
      "status": "paid"
    }
  }
}
```

### Email Service API

**1. Send Email Event**
```bash
GET /email/1

# Publishes email event to Kafka topic "sendEmail"
# EmailUtil processes and sends via Gmail SMTP
```

## Database Configuration

### MySQL Connection Details

**Default Configuration**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

### Current Service Port Notes

- `userservice` is configured on `9000`
- `paymentservice` is also configured on `9000`, so they cannot run together on the same machine without changing one of them
- `productservice` reads `SERVER_PORT` from the environment
- `emailservice` does not currently set `server.port`, so it uses Spring Boot's default unless you override it

### User Service Database Schema

```sql
-- Users Table
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(255),
  hashed_password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roles Table
CREATE TABLE role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  value VARCHAR(100) UNIQUE NOT NULL
);

-- User-Role Mapping (Many-to-Many)
CREATE TABLE user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES user(id),
  FOREIGN KEY (role_id) REFERENCES role(id)
);

-- Tokens Table
CREATE TABLE token (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  value VARCHAR(255) UNIQUE NOT NULL,
  expiry_at TIMESTAMP NOT NULL,
  user_id BIGINT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES user(id)
);
```

### Product Service Database Schema

```sql
-- Categories Table
CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products Table
CREATE TABLE product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  category_id BIGINT NOT NULL,
  description TEXT,
  quantity INT,
  is_deleted BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (category_id) REFERENCES category(id)
);

-- VIP Products (for inheritance)
CREATE TABLE vip_product (
  id BIGINT PRIMARY KEY,
  vip_discount DECIMAL(5, 2),
  FOREIGN KEY (id) REFERENCES product(id)
);
```

### Database Migrations

Product service includes Flyway migrations:
- `V1__init.sql` - Initial schema creation
- `V2__.sql` - Additional schema updates

Migrations are automatically applied on startup.

## Configuration Details

### Spring Boot Versions
- **Email Service**: 3.3.1
- **Payment Service**: 3.3.7
- **Product Service**: 3.2.5
- **User Service**: 3.3.0

### Port Configuration
```
User Service:    9000
Product Service: SERVER_PORT
Payment Service: 9000
Email Service:   8080 (default, unless overridden)
```

### Logging Levels
```properties
# User Service - Security audit logging
logging.level.org.springframework.security.*=TRACE

# Adjust as needed for debugging
logging.level.root=INFO
logging.level.com.example=DEBUG
```

### Kafka Configuration

**Email Service Consumer**:
- Topic: `sendEmail`
- Consumer Group: `emailService`
- Auto-offset-reset: earliest

**Kafka Producer** (Other services):
```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
```

### Redis Configuration

**Product Service Caching**:
```
- Default TTL: Depends on implementation
- Key pattern: product:*
- Benefits: Reduced DB load, faster searches
```

### Eureka Service Discovery

**Configuration**:
```properties
eureka.instance.hostname=localhost
eureka.client.registerWithEureka=true
eureka.client.fetchRegistry=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

**Registered Services**:
- Payment Service
- Product Service
- (Optional) User Service
- (Optional) Email Service

## Service Communication

### Inter-Service Communication Flow

```
1. Client → User Service
   └─ Authenticate user → Get token

2. Client → Product Service
   └─ Search/Browse products
   └─ (Uses Redis cache for performance)

3. Client → Payment Service
   └─ Initiate payment
   └─ Payment Service → Product Service
   └─ (Verify product exists via HTTP REST call)

4. Payment Service → Razorpay Gateway
   └─ Process payment

5. Razorpay → Payment Service Webhook
   └─ Payment confirmation

6. Any Service → Kafka
   └─ Publish events
   └─ Email Service ← Kafka
   └─ Consume events and send emails
```

### Service Discovery

- **Eureka Server**: Central service registry (localhost:8761)
- **Service Registration**: On startup, services register with Eureka
- **Service Discovery**: Services query Eureka for other service locations
- **Failover**: If service goes down, others get notified

### Communication Protocols

| Service Pair | Protocol | Type |
|---|---|---|
| Payment → Product | HTTP REST | Synchronous |
| Any Service → Kafka | AMQP | Asynchronous |
| Services → Eureka | HTTP | Registry |
| Services → Database | JDBC | Persistence |
| Email Service → SMTP | SMTP | External |
| Payment → Razorpay | HTTPS REST | External Gateway |

## Troubleshooting

### Common Issues

**1. MySQL Connection Error**
```
Error: Communications link failure
```
**Solution**:
- Ensure MySQL is running: `mysql -u root -p`
- Check connection URL in `application.properties`
- Verify username and password are correct
- Ensure database exists: `SHOW DATABASES;`

**2. Kafka Connection Error**
```
Error: Cannot connect to Kafka broker at localhost:9092
```
**Solution**:
- Start Zookeeper first, then Kafka broker
- Check Kafka is running: `jps` (should show Kafka process)
- Verify Kafka config: `config/server.properties`
- Reset Kafka: `cd kafka && rm -rf logs/`

**3. Port Already in Use**
```
Error: Address already in use: bind :9000
```
**Solution**:
```bash
# Find process using port 9000 on Windows
netstat -ano | findstr :9000

# Kill process
taskkill /PID <PID> /F

# Or change port in application.properties:
server.port=9002
```

**4. Service Discovery Not Working**
```
Error: RestTemplate call to service fails
```
**Solution**:
- Ensure Eureka server is running on port 8761
- Check service is registered: `http://localhost:8761`
- Verify service names in Eureka
- Check inter-service URLs are correct

**5. Redis Connection Error**
```
Error: Cannot connect to Redis at 6379
```
**Solution**:
- Start Redis: `redis-server`
- Check Redis is running: `redis-cli ping` (should return PONG)
- Verify port 6379 is open
- Check `RedisTemplateConfig` configuration

**6. Email Service Not Consuming Events**
```
Error: No messages received from Kafka topic
```
**Solution**:
- Ensure Kafka is running
- Create topic if needed: `kafka-topics.sh --create --topic sendEmail --bootstrap-server localhost:9092`
- Check consumer group: `kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list`
- Verify Gmail credentials in EmailUtil
- Check email service log for errors

**7. Razorpay Payment Failures**
```
Error: Invalid API keys or Payment declined
```
**Solution**:
- Verify Razorpay test credentials in `application.properties`
- Use Razorpay test cards for testing
- Check webhook URL is publicly accessible
- Verify webhook signature in webhook handler

### Debug Tips

**1. Enable Debug Logging**
```properties
logging.level.root=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.data=DEBUG
logging.level.org.springframework.security=DEBUG
```

**2. Check Service Health**
```bash
# User Service
curl http://localhost:9000/actuator/health

# Product Service
curl http://localhost:8080/actuator/health
```

**3. View Eureka Dashboard**
Open browser to: `http://localhost:8761/`

**4. Monitor Kafka Topics**
```bash
# List all topics
kafka-topics.sh --bootstrap-server localhost:9092 --list

# Consume messages
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic sendEmail --from-beginning
```

**5. Check MySQL Logs**
```bash
# View recent errors
tail -f /var/log/mysql/error.log
```

**6. Test Service Connectivity**
```bash
# Test User Service
curl -X POST http://localhost:9000/users/signup ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"name\":\"Test\",\"password\":\"pass123\"}"

# Test Product Service
curl http://localhost:8080/products
```

## Development Tips

### Adding New Features

**1. Add Database Entity**
- Create entity class in `src/main/java/com/example/.../models/`
- Extend `BaseModel` if available
- Use JPA annotations: `@Entity`, `@Column`, `@ManyToOne`, etc.

**2. Create Repository**
- Extend `JpaRepository<Entity, Long>`
- Place in `repositories/` package
- Define custom query methods

**3. Implement Service Logic**
- Create service class in `services/` package
- Inject repositories via `@Autowired`
- Implement business logic

**4. Create Controller**
- Create REST controller in `controllers/` package
- Use `@RestController`, `@RequestMapping`, `@GetMapping`, etc.
- Return DTOs, not entities

**5. Add Database Migration** (Product Service)
- Create `VX__description.sql` in `src/main/resources/db/migration/`
- Increment version number
- Flyway will run automatically

### Project Structure Best Practices

```
service/
├── src/main/java/com/example/
│   ├── config/           # Spring configurations, beans
│   ├── controllers/       # REST endpoints
│   ├── dtos/            # Data transfer objects
│   ├── models/          # JPA entities
│   ├── repositories/    # Data access layer
│   ├── services/        # Business logic
│   ├── security/        # Security configurations
│   ├── exceptions/      # Custom exceptions
│   └── Application.java # Main class
├── src/main/resources/
│   ├── application.properties
│   ├── application-dev.properties
│   ├── db/migration/    # Flyway migrations
│   ├── templates/       # Thymeleaf templates
│   └── static/         # Static files
└── pom.xml             # Maven configuration
```

## Security Considerations

⚠️ **Important Security Notes**:

1. **Hardcoded Credentials**: Email and Razorpay credentials are in properties files for demo purposes. Use environment variables in production.
   ```properties
   # Production approach
   spring.mail.password=${GMAIL_APP_PASSWORD}
   razorpay.key.secret=${RAZORPAY_KEY_SECRET}
   ```

2. **Password Encryption**: User passwords are hashed with BCrypt. Never store plain text passwords.

3. **Token Security**: Implement token refresh mechanism in production.
   ```java
   // Extend token validity
   expiryAt = LocalDateTime.now().plusDays(30);
   ```

4. **HTTPS**: Use HTTPS in production for all inter-service communication.

5. **CORS**: Configure CORS appropriately for frontend clients:
   ```java
   @Bean
   public WebMvcConfigurer corsConfigurer() {
       return new WebMvcConfigurer() {
           @Override
           public void addCorsMappings(CorsRegistry registry) {
               registry.addMapping("/api/**")
                   .allowedOrigins("http://localhost:3000")
                   .allowedMethods("*");
           }
       };
   }
   ```

## Performance Optimization

- **Caching**: Redis caching in Product Service reduces database load
- **Lazy Loading**: JPA relationships configured for optimal query performance
- **Connection Pooling**: HikariCP for efficient database connections
- **Kafka**: Asynchronous email processing prevents blocking
- **Service Discovery**: Eureka reduces need for hardcoded service URLs

## Contributing

When adding new features:
1. Follow existing code structure and naming conventions
2. Add appropriate logging: `logger.info()`, `logger.error()`
3. Include exception handling
4. Write unit tests
5. Update this README if adding new services or APIs
6. Use DTOs for REST responses, not entities
