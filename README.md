# Lending Management System POC

A backend proof of concept (POC) for a lending management platform built using Java and Spring Boot.

This project demonstrates the core backend architecture and financial transaction flow for a digital lending system, including:

* Loan application
* Loan approval
* Loan disbursement
* Loan repayment
* Penalty accrual
* Accounting entries

---

# Features

## Customer Management

* Customer registration
* Loan ownership tracking

## Loan Product Configuration

Supports configurable:

* Interest rate
* Penalty rate
* Processing fee
* Tenure
* Grace period

## Loan Application

* Validate loan product limits
* Calculate interest and fees
* Approve loans

## Loan Disbursement

* Principal disbursement
* Interest accrual
* Fee accrual

## Loan Repayment

Supports:

* Full repayment
* Partial repayment
* Early repayment
* Late repayment

Repayment allocation order:

1. Penalty
2. Fees
3. Interest
4. Principal

## Penalty Engine

* Detect overdue loans
* Apply penalties dynamically

## Accounting entries

Tracks:

* Disbursements
* Repayments
* Interest accruals
* Penalties

---

# Technology Stack

| Component             | Technology       |
| --------------------- | ---------------- |
| Language              | Java 21          |
| Framework             | Spring Boot 3    |
| ORM                   | Spring Data JPA  |
| Database              | MySQL 8          |
| Build Tool            | Maven            |
| Scheduling            | Spring Scheduler |
| Boilerplate Reduction | Lombok           |

---

# Project Structure

```text
src/main/java/io/ezra/lending

├── api
├── components
├── dtos
├── entities
├── repos
└── services
```

---

# Database Tables

| Table           		| Purpose                      |
| ---------------------	| ---------------------------- |
| customers       		| Customer information         |
| loan_products   		| Loan product configuration   |
| loans_applications    | Loan applications tracker    |
| loan_transactions		| Financial transaction ledger |
| loan_repayments 		| Loan repayment tracker       |

---

# Accounting Entries Architecture

These are append-only and immutable.

Financial events are stored as:

| Event            | Debit         | Credit      |
| ---------------- | ------------- | ----------- |
| Disbursement     | Increase debt |             |
| Interest accrual | Increase debt |             |
| Penalty accrual  | Increase debt |             |
| Repayment        |               | Reduce debt |

---

# Example Loan Flow

## Salary Advance Product

| Property       | Value   |
| -------------- | ------- |
| Principal      | 100,000 |
| Interest Rate  | 6%      |
| Penalty Rate   | 5%      |
| Processing Fee | 500     |
| Tenure         | 1 month |
| Grace Period   | 15 days |

---

# Example Disbursement Ledger Entries

| Entry Type       | Component | Debit  | Credit | Balance |
| ---------------- | --------- | ------ | ------ | ------- |
| DISBURSEMENT     | PRINCIPAL | 100000 | 0      | 100000  |
| INTEREST_ACCRUAL | INTEREST  | 6000   | 0      | 106000  |
| PROCESSING_FEE   | FEE       | 500    | 0      | 106500  |

Outstanding balance:

```text
106,500
```

---

# Installation Instructions

## Prerequisites

Ensure the following are installed:

* Java 21
* Maven
* MySQL 8

---

## Clone Repository

```bash
git clone https://github.com/harrymuhari/lending/tree/master/src/main/java/io/ezra/lending.git

cd lending
```

## Run Application

```bash
mvn spring-boot:run
```

Application starts on:

```text
http://localhost:8080
```

---

# API Documentation

Base URL:

```text
http://localhost:8080/api/v1
```

---

# Apply For Loan

## Endpoint

```http
POST /apply
```

## Request

```json
{
	"loanAmount":500000,
	"customerId":25901325,
	"loanProductId":101
}
```

## Response

```json
{
	"statusCode": 0,
	"message": "Loan application successful"
}
```

---

# Disburse Loan

## Endpoint

```http
POST /disburse
```

## Example

```json
{
	"loanReferenceId": 101259013251,
	"approvedBy": "harry"
}
```

## Response

```json
{
	"statusCode": 0,
	"message": "Loan disbursed successfully"
}
```

---

# Repay Loan

## Endpoint

```http
POST /repay
```

## Request

```json
{
	"loanReferenceId":101259013251,
	"amount":525500,
	"transactionReference":"KJSLFKLLAF"
}
```

## Response

```json
{
	"statusCode": 0,
	"message": "Loan repaid successfully"
}
```

---

# Penalty Processing

A scheduled job:

* Detects overdue loans
* Calculates penalties
* Posts penalty entries
* Updates loan balances

---

# Important Design Principles

## Immutable Accounting Entries

Entries are never deleted or updated.

Corrections should use reversal entries.

---

## Entries First

All balances originate from accounting entries.

Never directly mutate balances without postings entries.

---

## Snapshot Product Configuration

Loan product configurations are copied into loans at approval time to preserve contractual terms.

---

## Idempotency

Repayment transaction references must be unique to prevent duplicate postings.

---

# Future Improvements

Planned enhancements:

* JWT authentication
* Role-based access control
* Mobile money/bank integration
* Notification service
* Reversal engine
* Loan schedules
* Loan restructuring
* Reporting dashboard
* Audit logging