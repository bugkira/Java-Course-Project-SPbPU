-- V1__Create_users_table.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email_address VARCHAR(255) NOT NULL
);

CREATE INDEX idx_users_login ON users(login);
CREATE INDEX idx_users_email ON users(email_address);
