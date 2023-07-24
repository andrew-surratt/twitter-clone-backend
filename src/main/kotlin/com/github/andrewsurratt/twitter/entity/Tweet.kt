package com.github.andrewsurratt.twitter.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "tweet", schema = "twitter")
class Tweet() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tweet_id")
    lateinit var tweetId: UUID;
    @Column(name = "user_id")
    lateinit var userId: UUID;
    var tweet: String = "";
    var created: Date = Date();

    @OneToMany(mappedBy = "tweet")
    @JsonManagedReference
    var replies: List<Reply> = emptyList();

    constructor(
        userId: UUID,
        tweet: String
    ) : this() {
        this.userId = userId;
        this.tweet = tweet;
        this.created = Date();
    }
}
