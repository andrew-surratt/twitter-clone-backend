package com.github.andrewsurratt.twitter.services

import com.github.andrewsurratt.twitter.entity.User
import com.github.andrewsurratt.twitter.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

    fun getUserByUsername(username: String): Result<User> {
        val user = userRepository.findDistinctFirstByUsername(username)
        return if (user == null) {
            Result.failure(IllegalArgumentException("User with username $username not found"))
        } else {
            Result.success(user)
        }
    }
}
