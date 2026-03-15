# TestNova — Online Examination System

A desktop-based Online Examination System built with **Java Swing**, **PostgreSQL**, and **JDBC**.

![Java](https://img.shields.io/badge/Java-17+-orange)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue)
![JDBC](https://img.shields.io/badge/JDBC-4.2-green)

## 📋 Features

### Teacher (Admin)
- 📝 Create, edit, and delete exams
- ❓ Add multiple-choice questions with 4 options
- 📊 View all student results with filtering
- ✅ Activate/deactivate exams

### Student
- 🎓 Browse available exams
- ⏱ Take timed exams with countdown timer
- 📊 View own results with grades
- 🔄 Navigate between questions freely

## 🛠️ Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 17+ | [adoptium.net](https://adoptium.net/) |
| PostgreSQL | 15+ | [postgresql.org](https://www.postgresql.org/download/) |
| IntelliJ IDEA | 2023+ | [jetbrains.com](https://www.jetbrains.com/idea/) |
| PostgreSQL JDBC Driver | 42.7.x | [jdbc.postgresql.org](https://jdbc.postgresql.org/download/) |

## 🚀 Setup & Run

### 1. Database Setup

```bash
# Login to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE exam_system_db;

# Connect to it
\c exam_system_db

# Run the schema file
\i /path/to/online-exam-system/sql/schema.sql
```

### 2. Configure Database Connection

Edit `src/db/DatabaseConnection.java` if needed:
```java
private static final String URL = "jdbc:postgresql://localhost:5432/exam_system_db";
private static final String USERNAME = "postgres";
private static final String PASSWORD = "postgres";  // ← Change to your password
```

### 3. Open in IntelliJ IDEA

1. Open IntelliJ IDEA → **File** → **Open** → select the `online-exam-system` folder
2. Mark `src/` as **Sources Root** (right-click `src/` → **Mark Directory as** → **Sources Root**)
3. Add PostgreSQL JDBC Driver:
    - **File** → **Project Structure** → **Libraries** → **+** → **Java**
    - Browse to your downloaded `postgresql-42.7.3.jar`
4. Set JDK to 17+ in **Project Structure** → **Project** → **SDK**

### 4. Run

- Right-click `Main.java` → **Run 'Main.main()'**

## 🔐 Default Accounts

| Role | Username | Password |
|------|----------|----------|
| Teacher | `admin` | `admin123` |
| Student | `student1` | `student123` |

## 🎨 Color Scheme

| Color | Hex | Usage |
|-------|-----|-------|
| 🟢 Teal | `#0D9488` | Primary buttons, headers |
| 🟠 Coral | `#F97316` | Accents, highlights |
| ⬛ Charcoal | `#1E1E2E` | Backgrounds |
| ⬜ Cream | `#F5F5F0` | Text |

## 📁 Project Structure

```
online-exam-system/
├── src/
│   ├── Main.java                    # Entry point
│   ├── db/
│   │   └── DatabaseConnection.java  # JDBC connection
│   ├── model/
│   │   ├── User.java
│   │   ├── Exam.java
│   │   ├── Question.java
│   │   └── Result.java
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── ExamDAO.java
│   │   ├── QuestionDAO.java
│   │   └── ResultDAO.java
│   └── ui/
│       ├── UITheme.java             # Colors, fonts, components
│       ├── LoginScreen.java
│       ├── AdminDashboard.java
│       ├── StudentDashboard.java
│       ├── ManageExamsPanel.java
│       ├── ManageQuestionsPanel.java
│       ├── ExamScreen.java
│       └── ResultsPanel.java
└── sql/
    └── schema.sql                   # Database tables
```
