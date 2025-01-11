--liquibase formatted sql

--changeset mrshoffen:1
CREATE TABLE IF NOT EXISTS weather.locations
(
    id        INT PRIMARY KEY,
    name      VARCHAR(128)     NOT NULL,
    user_id   INT              NOT NULL REFERENCES weather.users,
    latitude  DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    CONSTRAINT user_unique_location_constraint UNIQUE (user_id, latitude, longitude)
);
