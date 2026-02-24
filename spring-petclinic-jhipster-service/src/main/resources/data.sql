-- JHipster Service Seed Data
-- Default users: admin/admin, user/user
-- Passwords are BCrypt encoded

-- Authorities
INSERT INTO jhi_authority (name) VALUES ('ROLE_ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO jhi_authority (name) VALUES ('ROLE_USER') ON CONFLICT DO NOTHING;

-- Admin user (password: admin)
INSERT INTO jhi_user (id, login, password_hash, first_name, last_name, email, activated, lang_key, created_by, created_date)
VALUES (1, 'admin', '$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC', 'Administrator', 'Administrator', 'admin@localhost', TRUE, 'en', 'system', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Regular user (password: user)
INSERT INTO jhi_user (id, login, password_hash, first_name, last_name, email, activated, lang_key, created_by, created_date)
VALUES (2, 'user', '$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7hasber3G', 'User', 'User', 'user@localhost', TRUE, 'en', 'system', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- System user
INSERT INTO jhi_user (id, login, password_hash, first_name, last_name, email, activated, lang_key, created_by, created_date)
VALUES (3, 'system', '$2a$10$mE.qmcV0mFU5NcKh73TZx.z4ueI/.bDWbj0T1BYh/BStZ/XAP3PFO', 'System', 'System', 'system@localhost', TRUE, 'en', 'system', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- User authorities
INSERT INTO jhi_user_authority (user_id, authority_name) VALUES (1, 'ROLE_ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO jhi_user_authority (user_id, authority_name) VALUES (1, 'ROLE_USER') ON CONFLICT DO NOTHING;
INSERT INTO jhi_user_authority (user_id, authority_name) VALUES (2, 'ROLE_USER') ON CONFLICT DO NOTHING;

-- Sample bank accounts
INSERT INTO bank_account (id, name, balance, user_id)
VALUES (1, 'Admin Account', 5000.00, 1)
ON CONFLICT DO NOTHING;

INSERT INTO bank_account (id, name, balance, user_id)
VALUES (2, 'User Account', 2500.50, 2)
ON CONFLICT DO NOTHING;

-- Sample labels
INSERT INTO label (id, label) VALUES (1, 'urgent') ON CONFLICT DO NOTHING;
INSERT INTO label (id, label) VALUES (2, 'info') ON CONFLICT DO NOTHING;

-- Sample operations
INSERT INTO operation (id, date, description, amount, bank_account_id)
VALUES (1, '2024-01-15 10:30:00', 'Initial deposit', 5000.00, 1)
ON CONFLICT DO NOTHING;

INSERT INTO operation (id, date, description, amount, bank_account_id)
VALUES (2, '2024-01-20 14:00:00', 'Payment', -50.00, 1)
ON CONFLICT DO NOTHING;

INSERT INTO operation (id, date, description, amount, bank_account_id)
VALUES (3, '2024-02-01 09:00:00', 'Salary', 2500.50, 2)
ON CONFLICT DO NOTHING;

-- Sample operation-label relationships
INSERT INTO rel_operation__label (operation_id, label_id) VALUES (1, 2) ON CONFLICT DO NOTHING;
INSERT INTO rel_operation__label (operation_id, label_id) VALUES (2, 1) ON CONFLICT DO NOTHING;
