--  1.Task Get numbers of vehicles sold (loans disbursed) in Jan and Feb 2020 per each vehicle make. 
--  a) Display only those makes where total sales are more than 1000 units

-- Declare dynamic variables
SET @start_date = '2020-01-01';
-- 2020 it was not a leap year so we go
SET @end_date = '2020-02-29';  
SET @totalSales = 1000;

-- Run the query using the variables
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





-- b) Display full sales data including all makes from database (including those where sales are not made)
-- Reuse the same date range
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





-- 2. Task

-- Get current weekly payment amount for each loan. Table m_loan_repayment_schedule contains weekly payment records. Weekly payment record should be selected for the first week where obligations are not met (value for field completed_derived=0). Use principal_amount plus interest_amount to acquire weekly payment amount.
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

-- 3. Task
-- Calculate current balance (scheduled amount - payed amount) for each loan. Use tables m_loan_repayment_schedule for payment schedule data. 
-- Total scheduled payment amount on current date must be calculated by sum of all amount field values. Some values can be null. Payment data are stored in table m_loan_transaction.
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
    
