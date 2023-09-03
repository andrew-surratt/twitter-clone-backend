package com.github.andrewsurratt.twitter.controller

import com.github.andrewsurratt.twitter.entity.Reply
import com.github.andrewsurratt.twitter.entity.Tweet
import com.github.andrewsurratt.twitter.repository.TweetRepository
import com.github.andrewsurratt.twitter.repository.UserRepository
import com.github.andrewsurratt.twitter.services.GoogleFactCheckService
import com.github.andrewsurratt.twitter.services.RepliesService
import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.jvm.optionals.getOrNull

@RestController
@CrossOrigin(originPatterns = ["http://localhost*"])
class TweetController {
    private val log: Log = LogFactory.getLog(TweetController::class.java.name)

    @Autowired
    private lateinit var tweetRepository: TweetRepository;

    @Autowired
    private lateinit var userRepository: UserRepository;

    @Autowired
    private lateinit var googleFactCheckService: GoogleFactCheckService;

    @Autowired
    private lateinit var repliesService: RepliesService

    @RequestMapping("/tweets")
    fun getTweets(): List<Tweet> {
        return tweetRepository.findAll(Sort.by(
            Sort.Order.desc("created")
        ));
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
        checkClaims(tweetResponse).getOrElse { e -> when (e) {
            is IllegalArgumentException -> log.warn(e.message, e)
            else -> log.error(e.message, e)
        } }
        return ResponseEntity.ok(tweetResponse)
    }

    private fun checkClaims(
        tweet: Tweet,
        botUsername: String = "umbot"
    ): Result<Reply?> {
        try {
            val checkClaim: GoogleFactCheckService.ClaimResponse? = googleFactCheckService.checkClaim(
                tweet.tweet
            )
            val claimReview = checkClaim?.claims?.getOrNull(0)?.claimReview?.getOrNull(0)
            if (claimReview != null) {
                val botUser = userRepository.findDistinctFirstByUsername(botUsername)
                    ?: return Result.failure(IllegalArgumentException("Bot $botUsername doesn't exist"))
                val botReply = repliesService.createReplyForUser(
                    tweet,
                    botUser,
                    "Um actually, that's ${claimReview.textualRating.lowercase()}. Source: ${claimReview.title}. ${claimReview.url}"
                )
                log.info("Created reply check for ${tweet.tweet}: ${botReply.map { it.replyId }} $checkClaim")
                return botReply
            }
            return Result.success(null)
        } catch (e: Exception) {
            log.error("Failed to check claim for tweet: ", e)
            return Result.failure(e)
        }
    }

    @GetMapping(
        value = ["/tweet/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getTweet(
        @PathVariable id: UUID,
        req: HttpServletRequest,
    ): ResponseEntity<Tweet> {
        val tweet = tweetRepository.findById(id).getOrNull()
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(tweet)
    }

    @DeleteMapping(
        value = ["/tweet/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun deleteTweet(
        @PathVariable id: UUID,
        req: HttpServletRequest,
    ): ResponseEntity<Tweet> {
        userRepository.findDistinctFirstByUsername(req.userPrincipal.name)
            ?: return ResponseEntity.badRequest().build()
        val tweet = tweetRepository.findById(id).getOrNull()
            ?: return ResponseEntity.badRequest().build()
        tweetRepository.delete(tweet)
        return ResponseEntity.ok().build()
    }

    class TweetRequestBody(val tweetText: String)
}
