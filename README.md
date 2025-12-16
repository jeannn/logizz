# 🧠 Logizz – Quiz Management Web Application

## 🧾 Overview
**Logizz** is a **Spring Boot web application** developed to demonstrate the integration of **user authentication, role-based authorization, and dynamic web interfaces** using **Thymeleaf** and **Spring Security**.

The application allows users to **register, log in, take quizzes, and view results**, while administrators can **create, edit, and manage quiz questions**.  
The project focuses on clean MVC architecture, secure authentication, and server-side logic without relying on external databases.

---

## 🎯 Objectives
- Implement **secure user authentication** using Spring Security.
- Manage **role-based access control** (`USER` vs `ADMIN`).
- Design dynamic pages using **Thymeleaf templates**.
- Separate concerns using **controllers, services, and models**.
- Handle form submissions and validations on the server side.
- Build a complete quiz workflow from login to results display.

---

## 🧩 Key Features

### 🔹 Authentication & Authorization
- Custom login and registration pages.
- Password encryption using **BCrypt**.
- Session-based authentication.
- Role-based page access:
  - **USER**: take quizzes and view results.
  - **ADMIN**: manage quizzes (CRUD operations).
- Automatic admin account creation at startup.

---

### 🔹 Quiz Management
- **Quiz Questions** include:
  - Question text
  - Multiple answer options
  - Correct answer
- Admins can:
  - View all quiz questions
  - Add new questions
  - Edit existing questions
  - Delete questions
- Users can:
  - Answer quiz questions
  - Submit responses
  - View final score

---

### 🔹 Application Architecture

#### 📌 MVC Design
- **Model**: `User`, `Question`
- **Controller**:
  - `pagesController` → Handles Thymeleaf pages
  - `serviceController` → REST API endpoints
- **Service**:
  - `UserService` → authentication and user storage
  - `QuestionService` → quiz management logic

---

### 🔹 Validation & Security Logic
- Server-side validation using:
  - Manual validation logic
  - Jakarta Validation annotations where applicable
- Protected endpoints using Spring Security filters.
- Clear separation between public and protected routes.

---

## 🖥️ User Interface

The UI is built with **Thymeleaf + Bootstrap 5** and includes:

- **Login Page** – Default landing page
- **Registration Page** – User account creation
- **Profile Page** – Displays logged-in user information and role
- **Quiz Page** – Answer and submit quiz questions
- **Result Page** – Displays quiz score
- **Admin Pages**:
  - Quiz list
  - Add quiz
  - Edit quiz

All pages are dynamically rendered based on authentication status and user role.

---

## 🔐 Default Admin Account

An administrator account is automatically created when the application starts:

