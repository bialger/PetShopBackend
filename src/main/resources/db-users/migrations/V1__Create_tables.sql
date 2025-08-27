CREATE TABLE app_users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(50)  NOT NULL,
    owner_id BIGINT
);

INSERT INTO app_users (username, password, role, owner_id)
VALUES ('admin', '$2a$12$v7THVqJu.0GHBUrTUqAB5Odk/A8N8fncOrsnIFTtta54TxrDFQnU.', 'ROLE_ADMIN', NULL);
