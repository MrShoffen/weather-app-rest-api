--liquibase formatted sql

--changeset mrshoffen:1
CREATE SCHEMA IF NOT EXISTS weather;

--changeset mrshoffen:2
CREATE TABLE IF NOT EXISTS weather.users
(
    id       SERIAL PRIMARY KEY,
    username    VARCHAR(32) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    avatar_url VARCHAR(64)
);
