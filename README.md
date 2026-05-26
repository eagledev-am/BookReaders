# BookReaders API

> A production-ready Spring Boot REST API powering a book reader community platform — browse books, track reading progress, engage in discussions, and purchase books through a full commerce experience.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Environment Configuration](#environment-configuration)
- [Running the Application](#running-the-application)
- [API Reference](#api-reference)
- [Architecture](#architecture)
- [Key Features](#key-features)
- [Messaging & Events](#messaging--events)
- [File Upload System](#file-upload-system)
- [Security](#security)
- [Environment Variables](#environment-variables)
- [License](#license)

---

## Overview

BookReaders provides a complete backend for a book community platform:

| Domain | Capability |
|--------|-----------|
| **Auth & Users** | Registration, JWT authentication, profile management, role-based access |
| **Book Catalog** | Browse, search, and filter books, authors, and categories |
| **Reading Tracker** | Track progress, reading status, and ratings per book |
| **Social** | Discussion rooms, posts, comments, and likes |
| **Commerce** | Cart, addresses, checkout, orders, payments, and promocodes |
| **Admin** | Full back-office control over users, catalog, posts, and orders |
| **Notifications** | Async email delivery via RabbitMQ for auth and commerce events |
| **Files** | Upload and serve book covers, author photos, and avatars |

---

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 4.0.1 | Backend framework |
| Java | 21 | Programming language |
| PostgreSQL | — | Remote relational database (Neon / AWS RDS) |
| RabbitMQ | CloudAMQP | Async message broker |
| Spring Security + JWT | — | Authentication & authorization |
| Spring Data JPA | — | Data persistence |
| MapStruct | — | Entity ↔ DTO mapping |
| Lombok | — | Boilerplate reduction |
| SpringDoc OpenAPI | — | Swagger UI & API spec |
| Docker & Compose | — | Containerization & orchestration |
| Maven | — | Build tool |

---

## Getting Started

### Prerequisites

- **Docker** ≥ 20.10
- **Docker Compose** ≥ 2.0

> No local JDK or Maven installation required — the build runs inside Docker.

### Quick Start

```bash
# 1. Clone the repository
git clone <repository-url>
cd bookreaders

# 2. Set up environment
cp .env.example .env
nano .env   # fill in your credentials (see Environment Variables)

# 3. Build and run
docker compose up -d --build

# 4. Tail startup logs
docker logs -f bookreaders-app
```

The application is ready when you see:
```
Started BookreadersApplication in X.XXX seconds
```

Once running:

| Interface | URL |
|-----------|-----|
| API Base | http://localhost:8088/api/v1 |
| Swagger UI | http://localhost:8088/swagger-ui.html |
| OpenAPI Spec | http://localhost:8088/api-docs |
| Health Check | http://localhost:8088/actuator/health |

---

## Environment Configuration

This application connects exclusively to **remote hosted services** — no local database or broker setup required.

### Database (PostgreSQL)

```bash
DB_URL=jdbc:postgresql://your-host.com:5432/your_db?sslmode=require
DB_USER=your_username
DB_PASSWORD=your_password
```

### Message Broker (RabbitMQ / CloudAMQP)

```bash
RABBITMQ_HOST=your-instance.rmq.cloudamqp.com
RABBITMQ_PORT=5671             # 5671 for SSL
RABBITMQ_VIRTUAL_HOST=your-vhost
RABBITMQ_USERNAME=your-username
RABBITMQ_PASSWORD=your-password
RABBITMQ_SSL_ENABLED=true
```

### Email (SMTP)

```bash
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password   # Gmail: use an App Password
```

> Generate a Gmail App Password at [myaccount.google.com/apppasswords](https://support.google.com/accounts/answer/185833).

### Admin & Security

```bash
ADMIN_EMAIL=admin@bookreaders.com
ADMIN_PASSWORD=your-admin-password
JWT_SECRET_KEY=your-base64-secret   # generate with: openssl rand -base64 64
```

---

## Running the Application

```bash
# Start
docker compose up -d --build

# Stop
docker compose down

# Stop and remove volumes (deletes uploaded files)
docker compose down -v

# View logs
docker logs -f bookreaders-app

# Check status
docker compose ps
```

---

## API Reference

### Authentication

All protected endpoints require a Bearer token:

```
Authorization: Bearer <token>
```

**Obtain a token:**

```bash
# Register
curl -X POST http://localhost:8088/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{ "fullName": "John Doe", "email": "john@example.com", "password": "password123" }'

# Login
curl -X POST http://localhost:8088/api/v1/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{ "email": "john@example.com", "password": "password123" }'
```

### Endpoints

| Domain | Base Path | Access |
|--------|-----------|--------|
| Authentication | `/api/v1/auth` | Public |
| User Profile | `/api/v1/u` | User |
| Books | `/api/v1/books` | Public |
| Authors | `/api/v1/authors` | Public |
| Categories | `/api/v1/categories` | Public |
| Reading Progress & Ratings | `/api/v1/books/{bookUuid}/progress`, `/api/v1/books/{bookUuid}/rating` | User |
| Discussion Rooms | `/api/v1/books/{bookUuid}/discussion-room` | User |
| Posts | `/api/v1/rooms/{roomUuid}/posts` | User |
| Comments | `/api/v1/posts/{postUuid}/comments` | User |
| Likes | `/api/v1/posts/{postUuid}/like` | User |
| Commerce (Cart, Orders, Addresses) | `/api/v1/commerce` | User |
| Promocodes | `/api/v1/commerce/promocodes` | User / Admin |
| Files | `/api/v1/file` | Public |
| Admin — Users | `/api/v1/admin/users` | Admin |
| Admin — Books | `/api/v1/d/books` | Admin |
| Admin — Authors | `/api/v1/d/authors` | Admin |
| Admin — Posts | `/api/v1/admin/posts` | Admin |

### Response Format

Every endpoint returns a consistent envelope:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {},
  "timestamp": "2026-05-24T10:30:00"
}
```

```json
{
  "success": false,
  "message": "Descriptive error message",
  "data": null,
  "timestamp": "2026-05-24T10:30:00"
}
```

---

## Architecture

### Infrastructure

```
┌──────────────────────────────────────────┐
│         Docker Container (Local)         │
│  ┌────────────────────────────────────┐  │
│  │  BookReaders Spring Boot App       │  │
│  │            :8088                   │  │
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
              │                │
              ▼                ▼
   ┌─────────────────┐  ┌─────────────────┐
   │   PostgreSQL    │  │    RabbitMQ     │
   │    (Remote)     │  │  (CloudAMQP)    │
   │    Neon DB      │  │    (Remote)     │
   └─────────────────┘  └─────────────────┘
```

### Docker Build

A multi-stage Dockerfile keeps the final image lean:

- **Stage 1** — Maven build inside Alpine Linux
- **Stage 2** — Lightweight JRE runtime (~150MB final image)

### Commerce Flow

```
Add to Cart
    ↓
Apply Promocode (optional)
    ↓
Select Address & Payment Method
    ↓
Order Created → PENDING
    ↓
Payment Processed (COD / Online)
    ↓
Order → PAID
    ↓  (via RabbitMQ)
    ├──→ Digital fulfillment (reading access granted)
    └──→ Receipt email sent
```

### Design Patterns

| Pattern | Usage |
|---------|-------|
| DTO | Clean separation between entities and API contracts |
| Service Layer | Business logic decoupled from controllers |
| Repository | Data access abstraction via Spring Data JPA |
| Mapper (MapStruct) | Compile-time entity ↔ DTO conversion |
| Strategy | Pluggable payment providers (`PaymentProvider` interface) |
| Event-Driven | RabbitMQ async messaging for fulfillment and notifications |
| Global Exception Handler | Standardized error responses across all endpoints |

---

## Key Features

### Authentication & Authorization
- Email verification on registration
- JWT-based stateless authentication
- Role-based access control: `USER`, `ADMIN`
- Secure password hashing with BCrypt
- Password reset via email

### Book Catalog
- Paginated browsing with sorting
- Full-text search by title, author
- Filter by category, language, and publication year
- Admin: soft-delete with restore support

### Reading Tracker
- Page-level progress tracking with completion percentage
- Status management: `WANT_TO_READ` → `READING` → `COMPLETED` 
- Per-book star ratings (1–5)

### Social
- One discussion room auto-created per book
- Posts with pagination
- Two-level nested comments
- Post likes
- Admin post moderation

### Commerce
- Persistent cart with add / remove / clear
- Multi-address book with default selection
- Checkout with optional promocode discount
- Payment methods: Cash on Delivery, Online (mock provider)
- Order history with item-level detail
- Admin: list, filter, inspect, and update order status
- Async fulfillment: reading access granted on payment

### Admin
- User management: list, search, create, update, block, delete
- Catalog management: books and authors including soft-deleted records
- Post moderation: list, search, delete
- Promocode management: full CRUD with search
- Order management: list, search, status updates

---

## Messaging & Events

All emails are delivered asynchronously via RabbitMQ.

| Queue | Routing Key | Trigger |
|-------|-------------|---------|
| `USER_EMAIL_QUEUE` | `EMAIL_KEY` | Registration verification, password reset |
| `COMMERCE_PAYMENT_RECEIPT_QUEUE` | `COMMERCE_PAYMENT_ROUTING_KEY` | Payment completed → receipt email |
| `COMMERCE_PAYMENT_FULFILLMENT_QUEUE` | `COMMERCE_PAYMENT_ROUTING_KEY` | Payment completed → digital access grant |
| `COMMERCE_PAYMENT_CANCELLED` | `COMMERCE_PAYMENT_CANCELLED_ROUTING_KEY` | Order cancelled → notification email |

---

## File Upload System

| Type | Storage Path | Served At |
|------|-------------|-----------|
| Book covers | `/uploads/book/` | `/api/v1/file/book/{fileName}` |
| Author photos | `/uploads/author/` | `/api/v1/file/author/{fileName}` |
| User avatars | `/uploads/avatar/` | `/api/v1/file/avatar/{fileName}` |

- Max file size: **5 MB**
- Accepted formats: **JPG, PNG, GIF**
- Storage is mounted as a Docker volume for persistence across restarts

---

## Security

| Feature | Implementation |
|---------|---------------|
| Authentication | JWT Bearer tokens |
| Password storage | BCrypt hashing |
| Email verification | Required before account activation |
| Authorization | Role-based (`USER`, `ADMIN`) per endpoint |
| Transport encryption | SSL/TLS for PostgreSQL and RabbitMQ connections |
| Input validation | Jakarta Bean Validation on all request DTOs |

---

## Environment Variables

### Required

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://host:5432/db?sslmode=require` |
| `DB_USER` | Database username | `neondb_owner` |
| `DB_PASSWORD` | Database password | `••••••••` |
| `RABBITMQ_HOST` | RabbitMQ hostname | `shark.rmq.cloudamqp.com` |
| `RABBITMQ_PORT` | RabbitMQ port | `5671` |
| `RABBITMQ_VIRTUAL_HOST` | RabbitMQ virtual host | `your-vhost` |
| `RABBITMQ_USERNAME` | RabbitMQ username | `your-username` |
| `RABBITMQ_PASSWORD` | RabbitMQ password | `••••••••` |
| `RABBITMQ_SSL_ENABLED` | Enable SSL for broker connection | `true` |
| `MAIL_USERNAME` | SMTP sender address | `you@gmail.com` |
| `MAIL_PASSWORD` | SMTP password or App Password | `••••••••` |
| `ADMIN_EMAIL` | Seeded admin account email | `admin@bookreaders.com` |
| `ADMIN_PASSWORD` | Seeded admin account password | `••••••••` |
| `JWT_SECRET_KEY` | Base64-encoded JWT signing key | `openssl rand -base64 64` |

### Optional

| Variable | Description | Default |
|----------|-------------|---------|
| `APP_BASE_URL` | Public base URL of the application | `http://localhost:8088` |
| `APPLICATION_SECURITY_JWT_EXPIRATION` | JWT expiration in milliseconds | `86400000` (24 h) |

---

## Project Structure

```
bookreaders/
├── src/main/java/com/eagledev/bookreaders/
│   ├── config/          # Security, RabbitMQ, and app configuration
│   ├── controllers/     # REST controllers (user-facing and admin)
│   ├── dtos/            # Request and response DTOs
│   ├── entities/        # JPA entities
│   ├── exceptions/      # Custom exceptions and global handler
│   ├── mappers/         # MapStruct interfaces
│   ├── repos/           # Spring Data JPA repositories
│   ├── services/        # Business logic and event listeners
│   └── util/            # Shared utilities
├── src/main/resources/
│   ├── application.properties
│   └── templates/       # Thymeleaf email templates
├── uploads/             # Persistent file storage (Docker volume)
├── Dockerfile           # Multi-stage build
├── docker-compose.yml
├── .env.example
└── pom.xml
```

---

## License

This project is currently unlicensed. Contact [magdyabdo484@gmail.com](mailto:magdyabdo484@gmail.com) for licensing information.

---

<p align="center">Built with ❤️ by <a href="mailto:magdyabdo484@gmail.com">Abdelrahman Magdy</a> &nbsp;·&nbsp; Happy Reading 📚</p>