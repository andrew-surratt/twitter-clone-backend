package com.github.andrewsurratt.twitter.services

import com.github.andrewsurratt.twitter.entity.Reply
import com.github.andrewsurratt.twitter.entity.Tweet
import com.github.andrewsurratt.twitter.entity.User
import com.github.andrewsurratt.twitter.repository.RepliesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RepliesService {
    @Autowired
    private lateinit var repliesRepository: RepliesRepository

    fun createReplyForUser(
        tweet: Tweet,
        user: User,
        replyText: String
    ): Result<Reply> {
        return Result.success(repliesRepository.save(
            Reply(
                tweet,
                user,
                replyText
            )
        ))
    }
}
