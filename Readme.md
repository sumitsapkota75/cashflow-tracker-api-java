# Machine Tracker API

Backend system for tracking cash movement in gaming machines across multiple business locations. It is designed to be linked with the frontend application at `https://github.com/sumitsapkota75/cashflow-tracker-frontend`.

## Quick Start

### Run with Docker

1. Build the image:

```bash
docker build -t machine-tracker-api .
```

2. Run the container:

```bash
docker run -p 8080:8080 machine-tracker-api
```

### Run with Maven

```bash
./mvnw spring-boot:run
```

If you prefer your local Maven installation:

```bash
mvn spring-boot:run
```

## Purpose of the System

This system is designed to track all cash movement in gaming machines across multiple business locations.

The key goals:

- Track daily machine periods (open → close)
- Handle multiple machine openings during a day
- Track cash in, cash out, safe drops, payouts, shortages
- Support delayed payout plans for big winners
- Provide clean, reliable reporting
- Prevent data corruption and human errors

This is a cash-critical system, so correctness > convenience.

## Core Domain Concepts (Mental Model)

Think of the system like this:

Business → Day (Period) → Machine Events → Cash Movements → Reports

Important Rule

💡 All cash movements must belong to exactly ONE business and ONE business day (Period).

## High-Level Architecture

Client (Web / Mobile)
|
v
REST Controllers (Thin)
|
v
Services (Business Rules & Validation)
|
v
Repositories (MongoDB)
|
v
MongoDB Collections

Supporting Layers

- DTOs → API contracts
- MapStruct → Entity ↔ DTO mapping
- Enums → State control
- ControllerAdvice → Global error handling

## MongoDB Collections (Source of Truth)

### 1. User Collection

`users`

Purpose

- Employees, managers, owners
- Track who opened/closed periods and machines

Key Fields

- id
- email (unique)
- role (ADMIN, EMPLOYEE)
- businessId
- active

### 2. Business Collection

`businesses`

Purpose

- Represents a physical store/location
- Owns machines, periods, users

Key Fields

- id
- name
- location
- numberOfMachines
- active
- createdAt / updatedAt

### 3. Period Collection (Most Important)

`periods`

Purpose

- Represents one business day
- Aggregates all cash activity for reporting

Business Rule

✅ Only ONE OPEN period per business per businessDate

Key Fields

- id
- businessId
- businessDate (LocalDate)
- status (OPEN, CLOSED)
- totalCashInOpen / totalCashOutOpen
- totalCashInClose / totalCashOutClose
- openedAt / closedAt
- openedByUserId / closedByUserId

📌 `businessDate` is NOT time. It represents the accounting day.

Example: `2026-01-16`

Used for reports, audits, and queries.

### 4. Machine Collection

`machines`

Purpose

- Represents each physical gaming machine

Key Fields

- id
- businessId
- machineNumber (1–N)
- active

### 5. Machine Entry / Machine Open Event

`machine_entries`

Purpose

- Tracks every time a machine is opened
- Happens multiple times per day
- Always linked to a Period

Key Fields

- id
- periodId
- businessId
- machineId
- openedAt
- openedByUserId
- cashCollectedPhysical
- machineCashIn
- machineCashOut
- net
- safeDrop
- payout
- reason (LOW_CASH, PAYOUT, BANK_DEPOSIT, PERIOD_CLOSE)

💡 These events roll up into Period totals.

### 6. Winner & Payout Plan

`winners`, `payout_plans`, `payout_transactions`

Why separate?

- Big wins are liabilities
- Payments happen over time
- Must be auditable

Winner

- id
- businessId
- machineId
- winAmount
- createdAt

Payout Plan

- id
- winnerId
- totalAmount
- remainingAmount
- status (ACTIVE, COMPLETED)

Payout Transaction

- id
- payoutPlanId
- periodId
- amountPaid
- paidAt
- paidByUserId

## API Design Philosophy

Controllers

- No business logic
- No DB access
- Only request → response

Services

- Validate rules
- Enforce state transitions
- Own lifecycle logic

Repositories

- Mongo queries only
- No validation

## Period Lifecycle (Critical)

CREATE → OPEN → (Machine Entries) → CLOSE → READ-ONLY

Rules

- ❌ Cannot open two periods for same businessDate
- ❌ Cannot close already closed period
- ❌ Machine entries only allowed when period is OPEN

## Reporting Strategy (Future-Proof)

Reports will be generated from:

- Period (daily summary)
- Machine Entries (drill-down)
- Payout Transactions (liabilities)

Because:

- businessDate is indexed
- Period aggregates are stable
- Machine entries are immutable
- Payouts are normalized

This supports:

- Daily profit
- Cash shortage
- Employee responsibility
- Payout obligations

## Why This Architecture Is Safe

- Auditable
- Scalable
- Mongo-friendly
- Reporting-ready
- Prevents double counting
- Prevents silent data corruption

This design matches real casino / ATM / retail cash systems.

## How to Continue Development

Recommended build order:

- Period (DONE)
- Machine entity
- Machine Entry flow
- Payout plan & installments
- Reporting APIs
- Frontend dashboards

## How to Use This With ChatGPT Later

When you come back, just say:

“Here is the architecture doc for my machine cash tracking system”

Paste this document and I will:

- Instantly understand the domain
- Continue coding without re-explaining
- Design APIs, reports, or frontend logic consistently
