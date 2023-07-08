package com.github.andrewsurratt.twitter.repository

import com.github.andrewsurratt.twitter.entity.UserAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAuthRepository : JpaRepository<UserAuth, String>
