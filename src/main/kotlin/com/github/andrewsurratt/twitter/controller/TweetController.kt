package com.github.andrewsurratt.twitter.controller

import com.github.andrewsurratt.twitter.entity.Tweet
import com.github.andrewsurratt.twitter.repository.TweetRepository
import com.github.andrewsurratt.twitter.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam
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

    @RequestMapping("/tweet/create")
    fun createTweet(
        @RequestParam(name = "tweet") tweet: String,
        req: HttpServletRequest,
    ): List<Tweet> {
        val user = userRepository.findDistinctFirstByUsername(req.userPrincipal.name)
        return listOf(
            tweetRepository.save(
                Tweet(
                    user.user_id,
                    tweet
                )
            )
        );
    }

}
