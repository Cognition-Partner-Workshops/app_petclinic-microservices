-- JHipster Service Database Schema
-- Adapted from JHipster monolith Liquibase migrations for HSQLDB

CREATE SEQUENCE IF NOT EXISTS sequenceGenerator START WITH 1050 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS jhi_authority (
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_jhi_authority PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS jhi_user (
    id BIGINT NOT NULL,
    login VARCHAR(50) NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(254),
    activated BOOLEAN NOT NULL,
    lang_key VARCHAR(10),
    image_url VARCHAR(256),
    activation_key VARCHAR(20),
    reset_key VARCHAR(20),
    reset_date TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    created_date TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP,
    CONSTRAINT pk_jhi_user PRIMARY KEY (id),
    CONSTRAINT ux_user_login UNIQUE (login),
    CONSTRAINT ux_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS jhi_user_authority (
    user_id BIGINT NOT NULL,
    authority_name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_jhi_user_authority PRIMARY KEY (user_id, authority_name),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES jhi_user (id),
    CONSTRAINT fk_authority_name FOREIGN KEY (authority_name) REFERENCES jhi_authority (name)
);

CREATE TABLE IF NOT EXISTS bank_account (
    id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    balance DECIMAL(21, 2) NOT NULL,
    user_id BIGINT,
    CONSTRAINT pk_bank_account PRIMARY KEY (id),
    CONSTRAINT fk_bank_account_user FOREIGN KEY (user_id) REFERENCES jhi_user (id)
);

CREATE TABLE IF NOT EXISTS label (
    id BIGINT NOT NULL,
    label VARCHAR(255) NOT NULL,
    CONSTRAINT pk_label PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS operation (
    id BIGINT NOT NULL,
    date TIMESTAMP NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(21, 2) NOT NULL,
    bank_account_id BIGINT,
    CONSTRAINT pk_operation PRIMARY KEY (id),
    CONSTRAINT fk_operation_bank_account FOREIGN KEY (bank_account_id) REFERENCES bank_account (id)
);

CREATE TABLE IF NOT EXISTS rel_operation__label (
    operation_id BIGINT NOT NULL,
    label_id BIGINT NOT NULL,
    CONSTRAINT pk_rel_operation__label PRIMARY KEY (operation_id, label_id),
    CONSTRAINT fk_rel_operation_label_operation FOREIGN KEY (operation_id) REFERENCES operation (id),
    CONSTRAINT fk_rel_operation_label_label FOREIGN KEY (label_id) REFERENCES label (id)
);
