package com.github.andrewsurratt.twitter.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.Clock
import java.time.Instant
import java.util.*

@Entity
@Table(name = "tweet", schema = "twitter")
class Tweet() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tweet_id")
    lateinit var tweetId: UUID;
    var tweet: String = "";
    var created: Instant = Instant.now(Clock.systemUTC());

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    lateinit var user: User;

    @OneToMany(mappedBy = "tweet", cascade = [CascadeType.REMOVE])
    @JsonManagedReference
    var replies: List<Reply> = emptyList();

    constructor(
        user: User,
        tweet: String,
        created: Instant = Instant.now(Clock.systemUTC())
    ) : this() {
        this.user = user;
        this.tweet = tweet;
        this.created = created;
    }
}
