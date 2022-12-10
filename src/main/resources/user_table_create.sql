DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS AUTHORITIES CASCADE;
DROP INDEX IF EXISTS ix_auth_username;

CREATE TABLE USERS
(
    username   VARCHAR(255) NOT NULL PRIMARY KEY,
    password   VARCHAR(255) NOT NULL,
    enabled    BOOLEAN      NOT NULL,
    first_name VARCHAR(255) NOT NULL
);

CREATE TABLE AUTHORITIES
(
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    FOREIGN KEY (username) REFERENCES USERS
        ON DELETE CASCADE
);


CREATE UNIQUE INDEX ix_auth_username
    ON AUTHORITIES (username, authority);
