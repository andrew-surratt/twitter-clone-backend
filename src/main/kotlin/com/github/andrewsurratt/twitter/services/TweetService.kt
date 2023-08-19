package com.github.andrewsurratt.twitter.services

import com.github.andrewsurratt.twitter.entity.Tweet
import com.github.andrewsurratt.twitter.repository.TweetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class TweetService {
    @Autowired
    private lateinit var tweetRepository: TweetRepository

    fun getTweetById(tweetId: UUID): Result<Tweet> {
        val tweet = tweetRepository.findById(tweetId).getOrNull()
        return if (tweet == null) {
            Result.failure(IllegalArgumentException("Tweet $tweetId not found"))
        } else {
            Result.success(tweet)
        }
    }
}
