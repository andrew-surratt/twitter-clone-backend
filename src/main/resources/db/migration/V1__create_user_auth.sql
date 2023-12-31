CREATE TABLE users
(
    username varchar(50)  NOT NULL PRIMARY KEY,
    password varchar(500) NOT NULL,
    enabled  boolean      NOT NULL
);

CREATE TABLE authorities
(
    username  varchar(50) NOT NULL,
    authority varchar(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username)
);
CREATE UNIQUE INDEX ix_auth_username ON authorities (username, authority);

CREATE TABLE groups
(
    id         bigint GENERATED BY DEFAULT AS IDENTITY (MINVALUE 0 START WITH 0) PRIMARY KEY,
    group_name varchar(50) NOT NULL
);

CREATE TABLE group_authorities
(
    group_id  bigint      NOT NULL,
    authority varchar(50) NOT NULL,
    CONSTRAINT fk_group_authorities_group FOREIGN KEY (group_id) REFERENCES groups (id)
);

CREATE TABLE group_members
(
    id       bigint GENERATED BY DEFAULT AS IDENTITY (MINVALUE 0 START WITH 0) PRIMARY KEY,
    username varchar(50) NOT NULL,
    group_id bigint      NOT NULL,
    CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES groups (id)
);
