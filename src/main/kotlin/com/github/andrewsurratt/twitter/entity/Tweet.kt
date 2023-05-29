package com.github.andrewsurratt.twitter.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "tweet", schema = "twitter")
class Tweet() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var tweet_id: UUID;
    var user_id: String = "";
    var tweet: String = "";
    var created: Date = Date();

    constructor(
        userId: String,
        tweet: String
    ) : this() {
        this.user_id = userId;
        this.tweet = tweet;
    }
}
