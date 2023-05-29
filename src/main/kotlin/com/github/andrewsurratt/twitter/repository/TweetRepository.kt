package com.github.andrewsurratt.twitter.repository

import com.github.andrewsurratt.twitter.entity.Tweet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
public interface TweetRepository : JpaRepository<Tweet, UUID>
