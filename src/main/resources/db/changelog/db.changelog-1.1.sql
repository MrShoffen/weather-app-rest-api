--liquibase formatted sql

--changeset mrshoffen:1
CREATE TABLE IF NOT EXISTS weather.sessions
(
    id         UUID PRIMARY KEY,
    user_id    INT       NOT NULL REFERENCES weather.users ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL
);
