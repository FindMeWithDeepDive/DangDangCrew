DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       nickname VARCHAR(100) UNIQUE NOT NULL,
                       phone_number VARCHAR(20) UNIQUE NOT NULL,
                       user_score DECIMAL(10,2) DEFAULT 0.00,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
