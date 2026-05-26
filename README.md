# RevTalent — Full-Stack AI-Powered HR Platform

> A comprehensive HR management system where employees manage leave, attendance, and performance reviews, while HR admins handle recruitment, onboarding, and workforce analytics. An AI assistant answers policy questions, screens resumes, and generates performance summaries using RAG over company documents.

---

## Repositories

| Layer | Link |
|-------|------|
| **Frontend** | [https://github.com/RevTalentAI-ABD/Frontend](https://github.com/RevTalentAI-ABD/Frontend) |

---

## Core Features

### 1. User Module
- Registration and JWT-based authentication
- Role-based access control: `EMPLOYEE`, `MANAGER`, `HR_ADMIN`, `CANDIDATE`

### 2. Employee Profiles
- Department, designation, and reporting hierarchy
- Document uploads per employee

### 3. Leave Management
- Apply/approve workflow: `APPLIED → APPROVED / REJECTED`
- Balance tracking and calendar view

### 4. Attendance
- Check-in / check-out logging with timestamps
- Monthly attendance summaries

### 5. Recruitment Pipeline
- Candidates can register and apply for open job postings
- Candidate tracking across stages: `APPLIED → SCREENING → INTERVIEW → OFFERED → HIRED`

### 6. Performance Reviews
- Manager-submitted ratings and feedback
- Flexible review templates per cycle stored in MongoDB

### 7. AI HR Assistant
- Answers policy questions from the employee handbook
- Screens resumes against job descriptions
- Summarizes performance history
- Drafts review narratives

---

## Tech Stack

| Layer | Technologies |
|-------|-------------|
| **Frontend** | React, Bootstrap, CSS, Axios, React Router |
| **Backend** | Spring Boot 3, Spring Security, JPA |
| **Relational DB** | MySQL — employees, leave, attendance, payroll |
| **Document DB** | MongoDB — resumes, reviews, policies |
| **AI Layer** | LangChain Python microservice, ChromaDB, Ollama |
| **Auth** | JWT tokens, BCrypt hashing |

---

## Deliverables

1. Responsive React UI with role-specific dashboards
2. Swagger-documented REST API
3. MySQL schema for HR transactional data
4. MongoDB collections for documents and reviews
5. LangChain microservice for policy Q&A and resume screening
6. JWT auth with hierarchical role access
7. Approval workflow engine for leave and recruitment
8. Full README with architecture diagram and API docs

---

## Getting Started

### Prerequisites

- Node.js 18+
- Java 21+
- MySQL 8+
- MongoDB 6+

### Frontend Setup

```bash
git clone https://github.com/RevTalentAI-ABD/Frontend
cd Frontend
npm install
npm start
```

### Backend Setup

```bash
# Configure application.properties with your MySQL and MongoDB credentials
cd backend
./mvnw spring-boot:run
```
