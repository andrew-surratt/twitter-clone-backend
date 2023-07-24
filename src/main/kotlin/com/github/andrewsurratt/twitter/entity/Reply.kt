package com.github.andrewsurratt.twitter.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "replies", schema = "twitter")
class Reply() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reply_id")
    lateinit var replyId: UUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id")
    @JsonBackReference
    lateinit var tweet: Tweet;

    @Column(name = "user_id")
    lateinit var userId: UUID;
    var reply: String = "";
    var created: Date = Date();

    constructor(
        tweet: Tweet,
        userId: UUID,
        reply: String
    ) : this() {
        this.tweet = tweet;
        this.userId = userId;
        this.reply = reply;
        this.created = Date();
    }
}
