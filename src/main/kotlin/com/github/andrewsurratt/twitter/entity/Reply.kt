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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    lateinit var user: User;

    var reply: String = "";
    var created: Date = Date();

    constructor(
        tweet: Tweet,
        user: User,
        reply: String
    ) : this() {
        this.tweet = tweet;
        this.user = user;
        this.reply = reply;
        this.created = Date();
    }
}
