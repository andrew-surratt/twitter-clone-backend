package com.github.andrewsurratt.twitter.repository

import com.github.andrewsurratt.twitter.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findDistinctFirstByUsername(username: String): User?
}
