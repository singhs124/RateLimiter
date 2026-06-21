CREATE TABLE IF NOT EXISTS rl_user (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    plan_type VARCHAR(50) NOT NULL,
    token VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS rl_user_api (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    key_hashed VARCHAR(255),
    key_prefix VARCHAR(255),
    key_lookup VARCHAR(255),
    status VARCHAR(100),
    plan_type VARCHAR(100),
    last_used_at TIMESTAMP,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_api_keys_user
        FOREIGN KEY (user_id)
        REFERENCES rl_user(user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_key_hash ON rl_user_api(key_hashed);