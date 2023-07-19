package com.github.andrewsurratt.twitter.controller

import com.github.andrewsurratt.twitter.entity.Tweet
import com.github.andrewsurratt.twitter.repository.TweetRepository
import com.github.andrewsurratt.twitter.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*

@RestController
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
    ): Tweet {
        val user = userRepository.findDistinctFirstByUsername(req.userPrincipal.name)
        return tweetRepository.save(
            Tweet(
                user.userId,
                tweet.tweetText
            )
        );
    }

    class TweetRequestBody(val tweetText: String)
}
