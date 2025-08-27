CREATE TABLE owners
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL
);

