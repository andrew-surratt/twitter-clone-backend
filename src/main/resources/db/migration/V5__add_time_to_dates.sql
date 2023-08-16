ALTER TABLE twitter.user
    ALTER COLUMN created TYPE timestamp;

ALTER TABLE twitter.tweet
    ALTER COLUMN created TYPE timestamp;

ALTER TABLE twitter.replies
    ALTER COLUMN created TYPE timestamp;
