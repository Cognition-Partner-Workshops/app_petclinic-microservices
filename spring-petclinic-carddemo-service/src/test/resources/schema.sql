DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS tran_cat_balances;
DROP TABLE IF EXISTS disclosure_groups;
DROP TABLE IF EXISTS card_xrefs;
DROP TABLE IF EXISTS accounts;

CREATE TABLE accounts (
    acct_id           BIGINT PRIMARY KEY,
    active_status     VARCHAR(1) NOT NULL,
    curr_bal          DECIMAL(12,2) NOT NULL,
    credit_limit      DECIMAL(12,2) NOT NULL,
    cash_credit_limit DECIMAL(12,2) NOT NULL,
    open_date         DATE,
    expiration_date   DATE,
    reissue_date      DATE,
    curr_cyc_credit   DECIMAL(12,2) NOT NULL,
    curr_cyc_debit    DECIMAL(12,2) NOT NULL,
    group_id          VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS card_xrefs (
    card_num VARCHAR(16) PRIMARY KEY,
    cust_id  BIGINT,
    acct_id  BIGINT
);

CREATE TABLE IF NOT EXISTS transactions (
    tran_id       VARCHAR(16) PRIMARY KEY,
    tran_type_cd  VARCHAR(2) NOT NULL,
    tran_cat_cd   INT NOT NULL,
    tran_source   VARCHAR(10),
    tran_desc     VARCHAR(100),
    tran_amt      DECIMAL(11,2) NOT NULL,
    merchant_id   BIGINT,
    merchant_name VARCHAR(50),
    merchant_city VARCHAR(50),
    merchant_zip  VARCHAR(10),
    card_num      VARCHAR(16) NOT NULL,
    orig_ts       TIMESTAMP,
    proc_ts       TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tran_cat_balances (
    acct_id      BIGINT NOT NULL,
    tran_type_cd VARCHAR(2) NOT NULL,
    tran_cat_cd  INT NOT NULL,
    tran_cat_bal DECIMAL(11,2) NOT NULL,
    PRIMARY KEY (acct_id, tran_type_cd, tran_cat_cd)
);

CREATE TABLE IF NOT EXISTS disclosure_groups (
    acct_group_id VARCHAR(10) NOT NULL,
    tran_type_cd  VARCHAR(2) NOT NULL,
    tran_cat_cd   INT NOT NULL,
    int_rate      DECIMAL(6,2) NOT NULL,
    PRIMARY KEY (acct_group_id, tran_type_cd, tran_cat_cd)
);
