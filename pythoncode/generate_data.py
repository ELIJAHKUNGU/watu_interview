start_id = 1009
total_records = 1050

for i in range(total_records):
    loan_id = start_id + i
    account_no = f"L{loan_id:03d}"
    disbursed_date = f"2020-01-{(i % 31) + 1:02d}" if i < 620 else f"2020-02-{(i % 29) + 1:02d}"
    registration_no = f"REG{loan_id:04d}"
    chassis_no = f"CH{loan_id:04d}"
    amount = 15000 + (i % 10) * 1000
    transaction_date = disbursed_date

    print(f"INSERT INTO `loan-schema`.`m_loan` (`id`, `account_no`, `disbursedon_date`) VALUES ({loan_id}, '{account_no}', '{disbursed_date}');")
    print(f"INSERT INTO `asset-schema`.`asset` (`id`, `model_id`, `m_loan_id`, `registration_no`, `chassis_no`) VALUES ({loan_id}, 1, {loan_id}, '{registration_no}', '{chassis_no}');")
    print(f"INSERT INTO `loan-schema`.`m_loan_transaction` (`id`, `loan_id`, `is_reversed`, `transaction_date`, `amount`) VALUES ({loan_id}, {loan_id}, 0, '{transaction_date}', {amount:.2f});")
