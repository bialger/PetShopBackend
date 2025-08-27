CREATE TABLE pets
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    birth_date  DATE         NOT NULL,
    breed       VARCHAR(255) NOT NULL,
    color       VARCHAR(50)  NOT NULL,
    tail_length DOUBLE PRECISION NOT NULL,
    owner_id    BIGINT
);

CREATE TABLE pet_friends
(
    pet_id    BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    PRIMARY KEY (pet_id, friend_id),
    CONSTRAINT fk_pet_id FOREIGN KEY (pet_id) REFERENCES pets (id) ON DELETE CASCADE,
    CONSTRAINT fk_friend_id FOREIGN KEY (friend_id) REFERENCES pets (id) ON DELETE CASCADE
);

