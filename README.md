# BookReaders API

A comprehensive Spring Boot REST API for managing a book reader community platform. The application enables users to browse books, track reading progress, participate in discussions, and interact through comments and likes.

## Overview

BookReaders is a full-featured backend service that provides:
- **User Management**: Registration, authentication, and profile management with JWT security
- **Book Catalog**: Browse books, authors, and categories with advanced search and filtering
- **Reading Tracking**: Monitor reading progress and rate books
- **Social Features**: Discussion rooms, posts, comments, and likes for community engagement
- **Email Notifications**: Async messaging via RabbitMQ for user verification and password resets
- **File Management**: Upload and manage book covers, author photos, and user avatars

---

## Tech Stack

| Technology | Purpose |
|------------|---------|
| **Spring Boot 4.0.1** | Backend framework |
| **Java 21** | Programming language |
| **PostgreSQL** | Remote relational database (Neon/AWS) |
| **RabbitMQ (CloudAMQP)** | Remote message broker for async tasks |
| **Spring Security + JWT** | Authentication and authorization |
| **Spring Data JPA** | Data persistence layer |
| **MapStruct** | Object mapping |
| **Lombok** | Boilerplate reduction |
| **SpringDoc OpenAPI** | API documentation (Swagger) |
| **Docker & Docker Compose** | Containerization and orchestration |
| **Maven** | Build tool |

---

## Prerequisites

Before running this application, ensure you have the following installed:

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)

---

##  Environment Configuration

### Step 1: Create Environment File

Copy the example environment file to create your local configuration:

```bash
cp .env.example .env
```

### Step 2: Configure Remote Services

**IMPORTANT**: This application uses **remote hosted services** for both the database and message broker:

#### Remote PostgreSQL Database (Required)
The application connects to a remote PostgreSQL database (e.g., Neon, AWS RDS, or similar). Update these variables in `.env`:

```bash
DB_URL=jdbc:postgresql://your-remote-db-host.com:5432/your_database?sslmode=require
DB_USER=your_db_username
DB_PASSWORD=your_db_password
```

#### Remote RabbitMQ (CloudAMQP) (Required)
The application uses a remote RabbitMQ service for async email processing. Configure these variables in `.env`:

```bash
# Remote RabbitMQ Configuration (CloudAMQP or other managed service)
RABBITMQ_HOST=your-instance.rmq.cloudamqp.com
RABBITMQ_PORT=5671                    # Use 5671 for SSL connections
RABBITMQ_VIRTUAL_HOST=your-vhost      # Often same as username in CloudAMQP
RABBITMQ_USERNAME=your-rabbitmq-user
RABBITMQ_PASSWORD=your-rabbitmq-pass
RABBITMQ_SSL_ENABLED=true             # Must be true for remote connections
```

#### Email Configuration (Required)
Configure SMTP settings for sending emails (Gmail example):

```bash
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-specific-password
```

> **Note**: For Gmail, you must generate an [App Password](https://support.google.com/accounts/answer/185833) instead of using your regular password.

#### Admin Account (Required)
Set the default admin credentials:

```bash
ADMIN_EMAIL=admin@bookreaders.com
ADMIN_PASSWORD=admin_password
```

#### JWT Secret (Required)
Generate a secure JWT secret key:

```bash
JWT_SECRET_KEY=your-generated-secret-key-here
```

---

## Running the Application

### Build and Start

Build the Docker image and start the application:

```bash
docker compose up -d --build
```

This command will:
1. Build the Spring Boot application using Maven inside a Docker container
2. Create an optimized production image
3. Start the application on port `8088`
4. Create persistent volumes for file uploads

### Check Status

Monitor the application startup:

```bash
# View application logs
docker logs -f bookreaders-app

# Check container status
docker compose ps
```

The application is ready when you see:
```
Started BookreadersApplication in X.XXX seconds
```

### Stop the Application

```bash
docker compose down
```

To remove volumes as well (will delete uploaded files):
```bash
docker compose down -v
```

---

## Accessing the Application

Once the application is running, you can access:

| Service | URL | Description |
|---------|-----|-------------|
| **API Endpoints** | http://localhost:8088/api/v1 | Base API path |
| **Swagger UI** | http://localhost:8088/swagger-ui.html | Interactive API documentation |
| **OpenAPI Docs** | http://localhost:8088/api-docs | OpenAPI 3.1 specification |
| **Health Check** | http://localhost:8088/actuator/health | Application health status |

---

## API Documentation

### Interactive Swagger UI

Visit the Swagger UI to explore and test all endpoints interactively:

```
http://localhost:8088/swagger-ui.html
```

### API Domain Overview

| Domain | Description | Base Path |
|--------|-------------|-----------|
| **Authentication** | User registration, login, verification, password reset | `/api/v1/auth` |
| **Users** | User profile management | `/api/v1/users` |
| **Books** | Book catalog, search, and filtering | `/api/v1/books` |
| **Authors** | Author management | `/api/v1/authors` |
| **Categories** | Book category management | `/api/v1/categories` |
| **Tracking** | Reading progress and book ratings | `/api/v1/books/{bookUuid}` |
| **Discussion Rooms** | Book-specific discussion spaces | `/api/v1/books/{bookUuid}/discussion-room` |
| **Posts** | Create and manage posts in discussion rooms | `/api/v1/rooms/{roomUuid}/posts` |
| **Comments** | Comment on posts and reply to comments | `/api/v1/posts/{postUuid}/comments` |
| **Likes** | Like posts | `/api/v1/posts/{postUuid}/like` |
| **Files** | Upload and serve images | `/api/v1/files` |

### Authentication

Most endpoints require JWT authentication. To authenticate:

1. **Register** a new user: `POST /api/v1/auth/register`
2. **Login** to get your JWT token: `POST /api/v1/auth/authenticate`
3. **Use the token** in subsequent requests:
   ```
   Authorization: Bearer {your-jwt-token}
   ```

---

##  Project Structure

```
bookreaders/
├── src/
│   ├── main/
│   │   ├── java/com/eagledev/bookreaders/
│   │   │   ├── config/           # Application configuration
│   │   │   ├── controllers/      # REST API endpoints
│   │   │   ├── dtos/             # Data Transfer Objects
│   │   │   ├── entities/         # JPA entities
│   │   │   ├── exceptions/       # Custom exceptions
│   │   │   ├── mappers/          # MapStruct mappers
│   │   │   ├── repos/            # JPA repositories
│   │   │   ├── services/         # Business logic
│   │   │   └── util/             # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/        # Email templates
│   └── test/                     # Unit and integration tests
├── uploads/                      # Persistent file storage
├── docker-compose.yml            # Docker orchestration
├── Dockerfile                    # Multi-stage build
├── .env.example                  # Environment template
└── pom.xml                       # Maven dependencies
```

---

### Database Management

Since the database is remote, you can connect directly using any PostgreSQL client:

```bash
# Using psql (if installed locally)
psql "jdbc:postgresql://your-remote-db-host.com:5432/your_database?sslmode=require" -U your_username
```

Or use GUI tools like:
- **pgAdmin**
- **DBeaver**
- **DataGrip**

---

## Security Features

- **JWT Authentication**: Secure token-based authentication with configurable expiration
- **Password Encryption**: BCrypt hashing for user passwords
- **Email Verification**: Users must verify their email before account activation
- **Role-Based Access**: Admin and User roles with different permissions
- **SSL/TLS**: Encrypted connections to PostgreSQL and RabbitMQ
- **Input Validation**: Jakarta Bean Validation on all request DTOs

---

## Email System

The application uses RabbitMQ for asynchronous email processing:

### Email Types
- **Verification Email**: Sent upon user registration
- **Password Reset**: Sent when users request password recovery

---

## File Upload System

The application supports image uploads for:
- **Book Covers** (`/uploads/book/`)
- **Author Photos** (`/uploads/author/`)
- **User Avatars** (`/uploads/avatar/`)

### Storage
Files are stored in the `./uploads` directory, which is mounted as a Docker volume for persistence.

### Limits
- **Max File Size**: 5MB per file
- **Max Request Size**: 5MB total
- **Supported Formats**: JPG, PNG, GIF

---

## Testing

### Health Check

Verify the application is running:

```bash
curl http://localhost:8088/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```



---

## Architecture Highlights

### Multi-Stage Docker Build
- **Stage 1**: Maven build in Alpine Linux container
- **Stage 2**: Lightweight JRE runtime image (~150MB final size)

### Remote Services Architecture
```
┌─────────────────────────────────────────┐
│      Docker Container (Local)           │
│  ┌───────────────────────────────────┐  │
│  │   BookReaders Spring Boot App     │  │
│  │         (Port 8088)               │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
              │              │
              │              │
              ▼              ▼
    ┌──────────────┐  ┌──────────────┐
    │  PostgreSQL  │  │   RabbitMQ   │
    │   (Remote)   │  │  (CloudAMQP) │
    │    Neon DB   │  │   (Remote)   │
    └──────────────┘  └──────────────┘
```

### Design Patterns
- **DTO Pattern**: Separation between entities and API contracts
- **Service Layer**: Business logic isolated from controllers
- **Repository Pattern**: Data access abstraction with JPA
- **Mapper Pattern**: MapStruct for entity-DTO conversion
- **Exception Handling**: Global exception handler with standardized responses

---

## Environment Variables Reference

### Required Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | Remote PostgreSQL JDBC URL | `jdbc:postgresql://host.com:5432/db?sslmode=require` |
| `DB_USER` | Database username | `neondb_owner` |
| `DB_PASSWORD` | Database password | `your-secure-password` |
| `RABBITMQ_HOST` | Remote RabbitMQ hostname | `shark.rmq.cloudamqp.com` |
| `RABBITMQ_PORT` | RabbitMQ port (SSL: 5671) | `5671` |
| `RABBITMQ_VIRTUAL_HOST` | Virtual host name | `your-vhost` |
| `RABBITMQ_USERNAME` | RabbitMQ username | `your-username` |
| `RABBITMQ_PASSWORD` | RabbitMQ password | `your-password` |
| `RABBITMQ_SSL_ENABLED` | Enable SSL for RabbitMQ | `true` |
| `MAIL_USERNAME` | SMTP email address | `your-email@gmail.com` |
| `MAIL_PASSWORD` | SMTP password (App Password) | `your-app-password` |
| `ADMIN_EMAIL` | Default admin email | `admin@bookreaders.com` |
| `ADMIN_PASSWORD` | Default admin password | `admin123` |
| `JWT_SECRET_KEY` | JWT signing key (base64) | `generate using openssl` |

### Optional Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `APP_BASE_URL` | Application base URL | `http://localhost:8088` |
| `APPLICATION_SECURITY_JWT_EXPIRATION` | JWT expiration (ms) | `86400000` (24h) |

---

## Quick Start Guide

### Clone the Repository
```bash
git clone <repository-url>
cd bookreaders
```

###  Configure Environment
```bash
# Copy environment template
cp .env.example .env

# Edit .env with your actual credentials
nano .env  # or use your preferred editor
```

### Start the Application
```bash
# Build and start in detached mode
docker compose up -d --build

# Monitor startup logs
docker logs -f bookreaders-app
```

### Access the API
Open your browser and navigate to:
- **Swagger UI**: http://localhost:8088/swagger-ui.html
- **Health Check**: http://localhost:8088/actuator/health

### Test Authentication
```bash
# Register a new user
curl -X POST http://localhost:8088/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'

# Login and get JWT token
curl -X POST http://localhost:8088/api/v1/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

---

## Key Features

###  Authentication & Authorization
- User registration with email verification
- JWT-based authentication
- Role-based access control (USER, ADMIN)
- Password reset via email
- Secure password encryption with BCrypt

###  Book Management
- Browse books with pagination and sorting
- Search books by title, author, or ISBN
- Filter by category, publication year, language
- View book details with author information
- Admin: Create, update, delete books

### Author & Category Management
- Author profiles with biography and photo
- Category-based book organization
- Admin: Full CRUD operations

### Reading Tracking
- Track reading progress (current page, percentage)
- Reading status: Want to Read, Reading, Completed, Dropped
- Rate books (1-5 stars)
- View personal reading statistics

###  Social Features
- **Discussion Rooms**: One per book, auto-created
- **Posts**: Share thoughts, reviews, questions
- **Comments**: Reply to posts and other comments (2-level depth)
- **Likes**: Express appreciation for posts
- **Pagination**: Efficient loading of large discussions

###  File Management
- Upload book covers, author photos, user avatars
- Automatic file validation and storage
- Serve static files via REST endpoints
- Persistent storage with Docker volumes

---

##  API Response Format

All endpoints return a standardized response:

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-02-23T10:30:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2026-02-23T10:30:00"
}
```
---

## Authors

Developed by the [EagleDev](mailto:magdyabdo484@gmail.com) team.

---

