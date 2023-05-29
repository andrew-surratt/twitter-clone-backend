CREATE SCHEMA IF NOT EXISTS twitter;
CREATE TABLE twitter.tweet (
    tweet_id uuid not null,
    user_id varchar not null,
    tweet varchar not null,
    created date not null,
    PRIMARY KEY (tweet_id)
);
