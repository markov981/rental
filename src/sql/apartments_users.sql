DROP TABLE IF EXISTS apartments_users;

CREATE TABLE apartments_users (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    apartment_id BIGINT,
    user_id BIGINT
);