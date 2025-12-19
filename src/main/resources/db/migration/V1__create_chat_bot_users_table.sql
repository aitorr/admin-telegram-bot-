-- Create chat_bot_users table
CREATE TABLE chat_bot_users (
    id BIGINT PRIMARY KEY,
    is_bot BOOLEAN NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    username VARCHAR(255),
    language_code VARCHAR(10)
);

-- Create index on username for faster lookups
CREATE INDEX idx_chat_bot_users_username ON chat_bot_users(username);
