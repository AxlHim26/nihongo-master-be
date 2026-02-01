# Japience - Japanese Language Learning Platform

<div align="center">

![Java](https://img.shields.io/badge/Java-21+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-alpine-DC382D?style=for-the-badge&logo=redis&logoColor=white)

A modern, full-featured backend API for Japanese language learning with vocabulary flashcards, grammar lessons, spaced repetition (SRS), and interactive games.

</div>

---

## 📖 Introduction

**Japience** is a backend REST API designed to power a Japanese language learning platform. It provides everything needed for vocabulary and grammar study, including:

- Vocabulary courses with flashcard-style learning
- Grammar books organized by chapters and points
- Spaced Repetition System (SRS) for optimized review scheduling  
- Speed Review game for interactive practice
- Video streaming from Google Drive
- JWT-based authentication

This project is ideal for developers building Japanese learning apps or students creating portfolio projects.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Spring Boot 3.2.5, Java 21+ |
| **Database** | PostgreSQL |
| **Cache** | Redis (for game sessions) |
| **Auth** | JWT (jjwt 0.11.5) |
| **Migrations** | Flyway |
| **Video Streaming** | Google Drive API |
| **Documentation** | SpringDoc OpenAPI (Swagger) |
| **Build** | Maven |
| **Containerization** | Docker Compose |

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Applications                       │
│                    (Web / Mobile / Desktop)                      │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Spring Boot Backend                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │ Controllers │  │  Services   │  │      Repositories       │  │
│  │             │  │             │  │                         │  │
│  │ • Auth      │  │ • SRS       │  │ • JPA (Hibernate)       │  │
│  │ • Vocab     │  │ • Game      │  │ • Query Methods         │  │
│  │ • Grammar   │  │ • Streaming │  │                         │  │
│  │ • Game      │  │             │  │                         │  │
│  │ • Video     │  │             │  │                         │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
         │                  │                       │
         ▼                  ▼                       ▼
   ┌──────────┐      ┌──────────┐           ┌─────────────┐
   │  Redis   │      │ Google   │           │ PostgreSQL  │
   │ (Cache)  │      │  Drive   │           │ (Database)  │
   └──────────┘      └──────────┘           └─────────────┘
```

---

## ✨ Main Features

### 📚 Vocabulary Learning
- CRUD operations for vocabulary courses
- Flashcard entries with term, reading, meaning, and examples
- JLPT level tagging (N5-N1)

### 📖 Grammar Lessons
- Grammar books organized by JLPT level
- Chapters with detailed grammar points
- Example sentences with readings

### 🔄 Spaced Repetition System (SRS)
- SM-2 algorithm implementation
- Automatic review scheduling
- Personal learning progress tracking

### 🎮 Speed Review Game
- Timed quiz-style gameplay
- Redis-backed game sessions
- Score tracking and results

### 📹 Video Streaming
- Stream videos from Google Drive
- HTTP Range support for seeking
- Memory-efficient streaming (no full buffering)

### 🔐 Authentication
- JWT-based stateless auth
- User registration and login
- Role-based access control

---

## 🚀 Installation & Run

### Prerequisites

- Java 21+ installed
- Docker & Docker Compose (for database and Redis)
- Maven (or use included wrapper)

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/japience.git
cd japience
```

### 2. Start Database & Redis

```bash
docker-compose up -d
```

This starts:
- PostgreSQL on port `5432`
- Redis on port `6379`

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The API will be available at: `http://localhost:8080`

### 4. Access Swagger UI

Open in browser: `http://localhost:8080/swagger-ui.html`

---

## ⚙️ Environment Configuration

### application.properties

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/elearning_db
spring.datasource.username=admin
spring.datasource.password=password

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT (change in production!)
jwt.secret-key=your-256-bit-secret-key-here
jwt.expiration=86400000

# Google Drive (optional)
google.drive.credentials-path=${GOOGLE_APPLICATION_CREDENTIALS:}
```

### Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `GOOGLE_APPLICATION_CREDENTIALS` | Path to Google Service Account JSON | For video streaming |
| `JWT_SECRET_KEY` | Override JWT secret | Production |

---

## 📋 Basic Usage

### Register a New User

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "learner",
    "email": "learner@example.com",
    "password": "securePassword123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "username": "learner",
    "password": "securePassword123"
  }'
```

### Get Vocabulary Review Cards

```bash
curl -X GET "http://localhost:8080/api/v1/vocab/review?limit=10" \
  -H "Authorization: Bearer <your-jwt-token>"
```

---

## 🔌 API Overview

| Endpoint | Method | Description | Auth |
|----------|--------|-------------|------|
| `/api/v1/auth/register` | POST | Register new user | ❌ |
| `/api/v1/auth/authenticate` | POST | Login and get JWT | ❌ |
| `/api/v1/vocab/courses` | GET | List vocabulary courses | ✅ |
| `/api/v1/vocab/courses/{id}` | GET | Get course details | ✅ |
| `/api/v1/vocab/review` | GET | Get cards due for review | ✅ |
| `/api/v1/vocab/review` | POST | Submit review rating | ✅ |
| `/api/v1/vocab/stats` | GET | Get review statistics | ✅ |
| `/api/v1/grammar-books` | GET | List all grammar books | ❌ |
| `/api/v1/chapters/{id}` | GET | Get chapter details | ❌ |
| `/api/v1/game/start` | POST | Start speed review game | ✅ |
| `/api/v1/game/answer` | POST | Submit game answer | ✅ |
| `/api/v1/videos/{id}/stream` | GET | Stream video content | ✅ |

> All responses follow a standard format: `{ "status": 200, "message": "...", "data": {...} }`

---

## 📁 Project Structure

```
japan-web/
├── src/main/java/com/example/japanweb/
│   ├── config/          # Spring configurations
│   ├── controller/      # REST controllers
│   │   ├── AuthController.java
│   │   ├── VocabController.java
│   │   ├── GrammarController.java
│   │   ├── GameController.java
│   │   └── VideoStreamController.java
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entities
│   │   ├── User.java
│   │   ├── VocabCourse.java
│   │   ├── VocabEntry.java
│   │   ├── GrammarBook.java
│   │   └── ...
│   ├── exception/       # Custom exceptions & handlers
│   ├── mapper/          # MapStruct mappers
│   ├── redis/           # Redis configurations
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # JWT & Spring Security
│   └── service/         # Business logic
│       ├── AuthenticationService.java
│       ├── VocabService.java
│       ├── SrsService.java
│       ├── GameService.java
│       └── GoogleDriveService.java
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/    # Flyway migrations
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 📸 Screenshots / Demo

> Screenshots are placeholders. Replace with actual screenshots after running the app.

| Swagger UI | API Response |
|------------|--------------|
| ![Swagger](https://via.placeholder.com/400x250?text=Swagger+UI) | ![Response](https://via.placeholder.com/400x250?text=API+Response) |

---

## 🔮 Future Improvements

- [ ] **WebSocket support** for real-time multiplayer games
- [ ] **OAuth2 login** (Google, GitHub)
- [ ] **Admin panel** for content management
- [ ] **Mobile push notifications** for review reminders
- [ ] **Progress analytics dashboard**
- [ ] **Audio pronunciation** for vocabulary
- [ ] **Sentence pattern practice** module
- [ ] **Unit tests** with Testcontainers

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Made with ❤️ for Japanese language learners

**[⬆ Back to Top](#japience---japanese-language-learning-platform)**

</div>
