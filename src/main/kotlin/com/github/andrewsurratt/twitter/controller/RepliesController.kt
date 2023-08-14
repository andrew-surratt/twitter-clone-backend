package com.github.andrewsurratt.twitter.controller

import com.github.andrewsurratt.twitter.entity.Reply
import com.github.andrewsurratt.twitter.entity.Tweet
import com.github.andrewsurratt.twitter.repository.RepliesRepository
import com.github.andrewsurratt.twitter.repository.TweetRepository
import com.github.andrewsurratt.twitter.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlin.jvm.optionals.getOrNull

@RestController
@CrossOrigin(originPatterns = ["http://localhost*"])
class RepliesController {
    private val log: Log = LogFactory.getLog(RepliesController::class.java.name)

    @Autowired
    private lateinit var repliesRepository: RepliesRepository;

    @Autowired
    private lateinit var tweetRepository: TweetRepository;

    @Autowired
    private lateinit var userRepository: UserRepository;

    @PostMapping(
        value = ["/reply"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createReply(
        @RequestBody reply: ReplyRequestBody,
        req: HttpServletRequest,
    ): ResponseEntity<Reply> {
        log.info("Creating reply with tweet id ${reply.tweetId}")
        val tweet: Tweet = tweetRepository.findById(reply.tweetId).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND, "Tweet with ID ${reply.tweetId} does not exist."
            )
        };
        log.info("Retrieved tweet")
        val user = userRepository.findDistinctFirstByUsername(req.userPrincipal.name)
            ?: return ResponseEntity.badRequest().build()
        val replyResponse = repliesRepository.save(
            Reply(
                tweet,
                user,
                reply.replyText
            )
        )
        return ResponseEntity.ok(replyResponse)
    }

    @GetMapping(
        value = ["/reply/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getReply(
        @PathVariable id: UUID,
        req: HttpServletRequest,
    ): ResponseEntity<Reply> {
        val replyResponse = repliesRepository.findById(id).getOrNull()
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(replyResponse)
    }

    class ReplyRequestBody(val tweetId: UUID, val replyText: String)
}
