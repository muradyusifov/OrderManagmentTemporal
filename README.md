Order Management System with Temporal & Spring Boot
A robust, resilient, and distributed order management microservice architecture built with Spring Boot 4.1.1, Temporal
Java SDK 1.31.0, PostgreSQL, and Apache Kafka. It implements advanced orchestration patterns including the Saga Pattern,
Asynchronous processing, Signals, Queries, Child Workflows, and Cron-based automated jobs.

Tech Stack
Backend Framework: Spring Boot 4.1.1 (Java 21)

Orchestration Engine: Temporal Java SDK 1.31.0

Database: PostgreSQL (Persistence for Temporal server and application data)

Messaging: Spring Kafka (Event-driven workflow triggers)

Utilities: Lombok, Spring Web & Data JPA

Architecture & Core Features
Saga Pattern & Compensation: Automatically handles distributed transaction rollbacks (e.g., executing payment refunds if
inventory reservation fails or the user rejects the order).

Asynchronous REST Processing: Non-blocking endpoints returning 202 Accepted while workflows execute safely in the
background.

Signals & Queries: Interactive workflow control via external signals (approveOrder, rejectOrder) and real-time state
inspection via Queries (getOrderStatus).

Child Workflows: Modularized subprocess execution (ShippingWorkflow running independently inside the main
OrderWorkflow).

Event-Driven Starters: Automatically triggers workflows via Kafka message listeners (order-created-events).

Scheduled/Cron Workflows: Server-managed periodic task execution (ReportWorkflow).

Prerequisites & Infrastructure
Ensure you have Docker and Docker Compose installed to run the local Temporal server and PostgreSQL backend.

API Endpoints
Create Order (Async): POST /orders/{id}?amount={amount}

Check Status (Query): GET /orders/{id}/status

Approve Order (Signal): POST /orders/{id}/approve

Reject Order (Signal): POST /orders/{id}/reject

Schedule Cron Report: POST /reports/schedule
