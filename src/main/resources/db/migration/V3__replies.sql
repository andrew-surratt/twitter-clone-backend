CREATE TABLE twitter.replies
(
    reply_id uuid    PRIMARY KEY NOT NULL,
    tweet_id uuid    NOT NULL,
    user_id  uuid    NOT NULL,
    reply    varchar NOT NULL,
    created  date    NOT NULL,
    FOREIGN KEY (tweet_id) REFERENCES twitter.tweet (tweet_id),
    FOREIGN KEY (user_id) REFERENCES twitter.user (user_id)
);
