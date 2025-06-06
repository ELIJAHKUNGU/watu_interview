
---

````markdown
# 🚗 Vehicle Loan Data & Scheduler Project

This project is organized into **three main folders**, each serving a distinct function in handling vehicle loan data, running scheduled actions, and performing SQL-based reporting.

---

## 📁 Folder Structure

```text
project-root/
├── pythoncode/
│   └── generate_data.py
├── springboot-scheduler/
│   └── (Spring Boot application files)
├── sql-tasks/
│   ├── sql_tasksanswers.sql
│   └── *.png (test evidence) --(ordered as per sql questions)
└── README.md
````

---

## 🐍 pythoncode

This folder contains a single Python file:

### `generate_data.py`

* **Purpose**: Generates initial test data required for SQL tasks 

```bash
python generate_data.py
```

---

## ☕ springboot-scheduler

A **Spring Boot application** that simulates a scheduled "action execution" process.

### 🔧 Features

* Uses a **scheduler** service that triggers based on a cron expression from `application.properties` (defaults to every minute).
* Reads from a **CSV file** with the following format:

```csv
time,bitmask
14:30,5
09:00,1
```

* `time` = time of day in `HH:mm` format
* `bitmask` = days of the week (e.g., 1 for Monday, 5 for Monday & Wednesday)


## 🧮 sql-tasks

Contains SQL queries and data setup for answering real-world vehicle loan analytics questions.

### 1️⃣ Vehicle Sales Reporting (Jan–Feb 2020)

#### a) Display only those makes where total sales are more than 1000 units



```sql
-- Declare dynamic variables
SET @start_date = '2020-01-01';
-- 2020 it was not a leap year so we go
SET @end_date = '2020-02-29';  
SET @totalSales = 1000;
SELECT 
    vmk.name AS vehicle_make,
    COUNT(DISTINCT a.id) AS vehicles_sold
FROM 
    `loan-schema`.m_loan_transaction t
JOIN `loan-schema`.m_loan l ON t.loan_id = l.id
JOIN `asset-schema`.asset a ON a.m_loan_id = l.id
JOIN `asset-schema`.vehicle_model vm ON a.model_id = vm.id
JOIN `asset-schema`.vehicle_make vmk ON vm.vehicle_make_id = vmk.id
WHERE 
    t.is_reversed = 0
    AND t.amount > 0
    AND t.transaction_date BETWEEN @start_date AND @end_date
GROUP BY 
    vmk.name
HAVING 
    COUNT(DISTINCT a.id) > @totalSales;
```

#### b) **Display full sales data including all makes from database (including those where sales are not made)**

```sql
SET @start_date = '2020-01-01';
SET @end_date = '2020-02-29';

SELECT 
    vmk.name AS vehicle_make,
    COUNT(DISTINCT a.id) AS vehicles_sold
FROM 
    `asset-schema`.vehicle_make vmk
LEFT JOIN `asset-schema`.vehicle_model vm ON vm.vehicle_make_id = vmk.id
LEFT JOIN `asset-schema`.asset a ON a.model_id = vm.id
LEFT JOIN `loan-schema`.m_loan l ON l.id = a.m_loan_id
LEFT JOIN `loan-schema`.m_loan_transaction t ON t.loan_id = l.id 
    AND t.is_reversed = 0
    AND t.amount > 0
    AND t.transaction_date BETWEEN @start_date AND @end_date
GROUP BY 
    vmk.name;
```

---

### 2️⃣ Weekly Payment Amount per Loan

Selects the **first unpaid weekly obligation** and returns the due date and total amount due:

```sql
SELECT 
    l.id AS loan_id,
    l.account_no,
    r.duedate,
    r.principal_amount + r.interest_amount AS weekly_payment_amount
FROM 
    `loan-schema`.`m_loan` l
JOIN 
    `loan-schema`.`m_loan_repayment_schedule` r ON l.id = r.loan_id
WHERE 
    r.completed_derived = 0
    AND r.duedate = (
        SELECT MIN(duedate)
        FROM `loan-schema`.`m_loan_repayment_schedule`
        WHERE loan_id = l.id
        AND completed_derived = 0
    )
ORDER BY 
    l.id;
```

---

### 3️⃣ Current Loan Balances

Calculates balance = **scheduled - paid** amount per loan:

```sql
SELECT 
    l.id AS loan_id,
    l.account_no,
    SUM(r.principal_amount + r.interest_amount + r.fee_charges_amount + r.penalty_charges_amount) AS scheduled_amount,
    SUM(t.amount) AS payed_amount,
    SUM(r.principal_amount + r.interest_amount + r.fee_charges_amount + r.penalty_charges_amount) - SUM(t.amount) AS current_balance
FROM 
    `loan-schema`.`m_loan` l
JOIN
    `loan-schema`.`m_loan_repayment_schedule` r ON l.id = r.loan_id
LEFT JOIN
    `loan-schema`.`m_loan_transaction` t ON l.id = t.loan_id
WHERE 
    r.duedate <= CURDATE()
GROUP BY
    l.id;
```

---






