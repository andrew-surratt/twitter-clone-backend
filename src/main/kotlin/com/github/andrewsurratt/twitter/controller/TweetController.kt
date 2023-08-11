package com.github.andrewsurratt.twitter.controller

import com.github.andrewsurratt.twitter.entity.Tweet
import com.github.andrewsurratt.twitter.repository.TweetRepository
import com.github.andrewsurratt.twitter.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.jvm.optionals.getOrNull

@RestController
@CrossOrigin(originPatterns = ["http://localhost*"])
class TweetController {
    @Autowired
    private lateinit var tweetRepository: TweetRepository;

    @Autowired
    private lateinit var userRepository: UserRepository;

    @RequestMapping("/tweets")
    fun getTweets(): List<Tweet> {
        return tweetRepository.findAll();
    }

    @PostMapping(
        value = ["/tweet"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createTweet(
        @RequestBody tweet: TweetRequestBody,
        req: HttpServletRequest,
    ): ResponseEntity<Tweet> {
        val user = userRepository.findDistinctFirstByUsername(req.userPrincipal.name)
            ?: return ResponseEntity.badRequest().build()
        val tweetResponse = tweetRepository.save(
            Tweet(
                user,
                tweet.tweetText
            )
        )
        return ResponseEntity.ok(tweetResponse)
    }

    @DeleteMapping(
        value = ["/tweet"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun deleteTweet(
        @RequestBody tweetReq: DeleteTweetRequestBody,
        req: HttpServletRequest,
    ): ResponseEntity<Tweet> {
        userRepository.findDistinctFirstByUsername(req.userPrincipal.name)
            ?: return ResponseEntity.badRequest().build()
        val tweet = tweetRepository.findById(tweetReq.tweetId).getOrNull()
            ?: return ResponseEntity.badRequest().build()
        tweetRepository.delete(tweet)
        return ResponseEntity.ok().build()
    }

    class TweetRequestBody(val tweetText: String)
    class DeleteTweetRequestBody(val tweetId: UUID)
}
