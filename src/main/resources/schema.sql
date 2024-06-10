DROP TABLE IF EXISTS exchange_rate;
DROP TABLE IF EXISTS users;

CREATE TABLE exchange_rate (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date_time TIMESTAMP NOT NULL,
    user_id VARCHAR(255),
    base_amount NUMERIC(38,2) NOT NULL,
    conversion_amount NUMERIC(38,2) NOT NULL,
    base_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    target_rate NUMERIC(38,6) NOT NULL
);

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN
);

INSERT INTO users (username, email, password, is_active)
VALUES
('user1', 'juan@rodriguez.org', '$2a$10$Mn0K2LvkpL47AVxVqTx0seXhdd0hKcd3xy4oUQW27FE50t6Az.5Vm', true); -- password: JeanPIERE123
