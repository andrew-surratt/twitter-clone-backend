package com.github.andrewsurratt.twitter.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "user", schema = "twitter")
class User() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    lateinit var userId: UUID;
    var username: String = "";
    var firstname: String = "";
    var lastname: String = "";
    var created: Date = Date();

    constructor(
        username: String,
        firstname: String,
        lastname: String,
    ) : this() {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.created = Date();
    }
}
