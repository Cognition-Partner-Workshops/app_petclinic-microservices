-- JHipster Service Seed Data
-- Default users: admin/admin, user/user
-- Passwords are BCrypt encoded
-- Uses HSQLDB-compatible MERGE INTO syntax for idempotent inserts

-- Authorities
MERGE INTO jhi_authority (name) KEY (name) VALUES ('ROLE_ADMIN');
MERGE INTO jhi_authority (name) KEY (name) VALUES ('ROLE_USER');

-- Admin user (password: admin)
MERGE INTO jhi_user (id, login, password_hash, first_name, last_name, email, activated, lang_key, created_by, created_date)
KEY (id)
VALUES (1, 'admin', '$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC', 'Administrator', 'Administrator', 'admin@localhost', TRUE, 'en', 'system', CURRENT_TIMESTAMP);

-- Regular user (password: user)
MERGE INTO jhi_user (id, login, password_hash, first_name, last_name, email, activated, lang_key, created_by, created_date)
KEY (id)
VALUES (2, 'user', '$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7hasber3G', 'User', 'User', 'user@localhost', TRUE, 'en', 'system', CURRENT_TIMESTAMP);

-- System user
MERGE INTO jhi_user (id, login, password_hash, first_name, last_name, email, activated, lang_key, created_by, created_date)
KEY (id)
VALUES (3, 'system', '$2a$10$mE.qmcV0mFU5NcKh73TZx.z4ueI/.bDWbj0T1BYh/BStZ/XAP3PFO', 'System', 'System', 'system@localhost', TRUE, 'en', 'system', CURRENT_TIMESTAMP);

-- User authorities
MERGE INTO jhi_user_authority (user_id, authority_name) KEY (user_id, authority_name) VALUES (1, 'ROLE_ADMIN');
MERGE INTO jhi_user_authority (user_id, authority_name) KEY (user_id, authority_name) VALUES (1, 'ROLE_USER');
MERGE INTO jhi_user_authority (user_id, authority_name) KEY (user_id, authority_name) VALUES (2, 'ROLE_USER');

-- Sample bank accounts
MERGE INTO bank_account (id, name, balance, user_id) KEY (id) VALUES (1, 'Admin Account', 5000.00, 1);
MERGE INTO bank_account (id, name, balance, user_id) KEY (id) VALUES (2, 'User Account', 2500.50, 2);

-- Sample labels
MERGE INTO label (id, label) KEY (id) VALUES (1, 'urgent');
MERGE INTO label (id, label) KEY (id) VALUES (2, 'info');

-- Sample operations
MERGE INTO operation (id, date, description, amount, bank_account_id) KEY (id) VALUES (1, '2024-01-15 10:30:00', 'Initial deposit', 5000.00, 1);
MERGE INTO operation (id, date, description, amount, bank_account_id) KEY (id) VALUES (2, '2024-01-20 14:00:00', 'Payment', -50.00, 1);
MERGE INTO operation (id, date, description, amount, bank_account_id) KEY (id) VALUES (3, '2024-02-01 09:00:00', 'Salary', 2500.50, 2);

-- Sample operation-label relationships
MERGE INTO rel_operation__label (operation_id, label_id) KEY (operation_id, label_id) VALUES (1, 2);
MERGE INTO rel_operation__label (operation_id, label_id) KEY (operation_id, label_id) VALUES (2, 1);
