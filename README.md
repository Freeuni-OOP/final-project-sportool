[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/skmUAHf8)
# Final-Project
# OOP ფინალური პროექტი
# SporTool

A full-stack web application designed to connect sports enthusiasts with trainers and athletic facilities. The platform enables trainers to manage their venues, availability slots, and custom pricing, while allowing players to browse and book sports fields or training sessions in real-time.


### Security and Architecture
* **Session Management:** Server-side user state tracking and role validation (Trainer vs. Player) utilizing Java HTTP Sessions.
* **Data Integrity:** Backend validation filters to secure data access and prevent unauthorized modifications or deletions.

## Tech Stack

* **Frontend:** React, JavaScript, Asynchronous API calling (Fetch / Axios)
* **Backend:** Java Servlets, Jackson (JSON Serialization)
* **Database:** PostgreSQL
* **Architecture:** DAO (Data Access Object) Design Pattern
* **Containerization:** Docker, Docker Compose
* **Testing:** JUnit, Mockito

## Architecture Design

The application separates business logic from database operations using the DAO pattern:
`React Frontend <-> Java Servlets (Controller & Session Validation) <-> Service Layer (Business Logic) <-> DAO Layer (SQL) <-> PostgreSQL`

## Team Workflow

The team followed a full-stack rotation workflow, meaning all members contributed to both the frontend (React) and backend (Java Servlets). This ensured shared knowledge across API integration, state management, and database design. Version control was managed entirely through Git using isolated branches to collaborate and handle merge conflicts.

## Installation and Setup

The application is fully containerized using Docker, removing the need for local Tomcat or PostgreSQL configurations.

### Prerequisites
* Docker and Docker Compose installed.

### Running the Application
1. Clone the repository.
2. Run the following command in the root directory:

```bash
docker compose up --build