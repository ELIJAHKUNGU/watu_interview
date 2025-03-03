--  1.Task Get numbers of vehicles sold (loans disbursed) in Jan and Feb 2020 per each vehicle make. 
--  a) Display only those makes where total sales are more than 1000 units

SET @start_date = '2020-01-01';
SET @end_date = '2020-02-29';
SET @total_sales = 1000;

SELECT 
    vm.name AS vehicle_make,
    COUNT(a.id) AS cars_sold
FROM `asset-schema`.`asset` a
JOIN `asset-schema`.`vehicle_model` vmo ON a.model_id = vmo.id
JOIN `asset-schema`.`vehicle_make` vm ON vmo.vehicle_make_id = vm.id
JOIN `loan-schema`.`m_loan` ml ON a.m_loan_id = ml.id
WHERE ml.disbursedon_date BETWEEN @start_date AND @end_date
GROUP BY vm.name
HAVING total_sales > @total_sales;



-- b) Display full sales data including all makes from database (including those where sales are not made)
DECLARE @start_date DATE = '2020-01-01';
DECLARE @end_date DATE = '2020-02-29';

SELECT 
    vm.name AS vehicle_make,
    COUNT(a.id) AS cars_sold
FROM asset-schema.vehicle_make vm
LEFT JOIN asset-schema.vehicle_model vmo 
    ON vm.id = vmo.vehicle_make_id
LEFT JOIN asset-schema.asset a 
    ON vmo.id = a.model_id
LEFT JOIN loan-schema.m_loan ml 
    ON a.m_loan_id = ml.id
    AND CAST(ml.disbursedon_date AS DATE) BETWEEN @start_date AND @end_date
GROUP BY vm.name;





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
-- Calculate current balance (scheduled amount - payed amount) for each loan. Use tables m_loan_repayment_schedule for payment schedule data. Total scheduled payment amount on current date must be calculated by sum of all amount field values. Some values can be null. Payment data are stored in table m_loan_transaction.
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
    
