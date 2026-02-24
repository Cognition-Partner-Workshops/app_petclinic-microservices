-- Sample data extracted from CardDemo COBOL app/data/ASCII/ files.
-- Account data from acctdata.txt (first 5 records)
-- COBOL layout: ACCT-ID(11), STATUS(1), CURR-BAL S9(10)V99, CREDIT-LIMIT S9(10)V99,
--   CASH-CREDIT-LIMIT S9(10)V99, OPEN-DATE X(10), EXP-DATE X(10), REISSUE-DATE X(10),
--   CYC-CREDIT S9(10)V99, CYC-DEBIT S9(10)V99, ADDR-ZIP X(10), GROUP-ID X(10)

INSERT INTO accounts (acct_id, active_status, curr_bal, credit_limit, cash_credit_limit,
    open_date, expiration_date, reissue_date, curr_cyc_credit, curr_cyc_debit, group_id)
VALUES
    (1, 'Y', 194.00, 2020.00, 1020.00, '2014-11-20', '2025-05-20', '2025-05-20', 0.00, 0.00, 'A000000000'),
    (2, 'Y', 158.00, 6130.00, 5448.00, '2013-06-19', '2024-08-11', '2024-08-11', 0.00, 0.00, 'A000000000'),
    (3, 'Y', 147.00, 4909.00, 538.00,  '2013-08-23', '2024-01-10', '2024-01-10', 0.00, 0.00, 'A000000000'),
    (4, 'Y', 40.00,  3503.00, 2789.00, '2012-11-17', '2023-12-16', '2023-12-16', 0.00, 0.00, 'A000000000'),
    (5, 'Y', 345.00, 3819.00, 2430.00, '2012-10-03', '2025-03-09', '2025-03-09', 0.00, 0.00, 'A000000000'),
    -- Additional accounts for transaction posting tests
    (27, 'Y', 500.00, 10000.00, 5000.00, '2013-01-01', '2025-12-31', '2025-12-31', 100.00, -50.00, 'A000000000'),
    (50, 'Y', 1000.00, 5000.00, 2500.00, '2014-01-01', '2025-06-30', '2025-06-30', 200.00, -100.00, 'A000000000');

-- Card cross-reference data from cardxref.txt
-- COBOL layout: XREF-CARD-NUM X(16), XREF-CUST-ID 9(09), XREF-ACCT-ID 9(11)
INSERT INTO card_xrefs (card_num, cust_id, acct_id) VALUES
    ('0500024453765740', 50, 50),
    ('0683586198171516', 27, 27),
    ('0923877193247330', 2,  2),
    ('0927987108636232', 20, 20),
    ('0982496213629795', 12, 12),
    -- Additional xrefs for accounts 1,3,4,5
    ('1000000000000001', 1,  1),
    ('3000000000000003', 3,  3),
    ('4000000000000004', 4,  4),
    ('5000000000000005', 5,  5);

-- Transaction category balance data from tcatbal.txt
-- COBOL layout: ACCT-ID 9(11), TYPE-CD X(02), CAT-CD 9(04), BAL S9(09)V99
INSERT INTO tran_cat_balances (acct_id, tran_type_cd, tran_cat_cd, tran_cat_bal) VALUES
    (1, '01', 1, 0.00),
    (2, '01', 1, 0.00),
    (3, '01', 1, 0.00),
    (4, '01', 1, 0.00),
    (5, '01', 1, 0.00),
    -- Non-zero balances for interest calculation testing
    (27, '01', 1, 1200.00),
    (27, '01', 2, 500.00),
    (50, '01', 1, 3000.00);

-- Disclosure group data from discgrp.txt
-- COBOL layout: GROUP-ID X(10), TYPE-CD X(02), CAT-CD 9(04), INT-RATE S9(04)V99
INSERT INTO disclosure_groups (acct_group_id, tran_type_cd, tran_cat_cd, int_rate) VALUES
    ('A000000000', '01', 1, 15.00),
    ('A000000000', '01', 2, 25.00),
    ('A000000000', '01', 3, 25.00),
    ('A000000000', '01', 4, 25.00),
    ('A000000000', '02', 1, 0.00),
    ('A000000000', '02', 2, 0.00),
    ('A000000000', '02', 3, 0.00),
    ('A000000000', '03', 1, 0.00),
    ('A000000000', '03', 2, 0.00),
    ('A000000000', '03', 3, 0.00),
    -- DEFAULT group for fallback testing
    ('DEFAULT', '01', 1, 12.00),
    ('DEFAULT', '01', 2, 18.00);
