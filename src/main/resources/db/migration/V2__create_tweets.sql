CREATE SCHEMA IF NOT EXISTS twitter;
CREATE TABLE twitter.user
(
    user_id uuid NOT NULL PRIMARY KEY,
    username varchar UNIQUE NOT NULL,
    firstname varchar NOT NULL,
    lastname varchar NOT NULL,
    created date NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username)
);
CREATE TABLE twitter.tweet
(
    tweet_id uuid    NOT NULL PRIMARY KEY,
    user_id  uuid    NOT NULL,
    tweet    varchar NOT NULL,
    created  date    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES twitter.user (user_id)
);
