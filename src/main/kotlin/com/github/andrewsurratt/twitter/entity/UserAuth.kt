package com.github.andrewsurratt.twitter.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserAuth() {
    @Id
    lateinit var username: String;

    constructor(
        username: String
    ) : this() {
        this.username = username;
    }
}
