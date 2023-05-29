package com.github.andrewsurratt.twitter.controller

import com.github.andrewsurratt.twitter.entity.Tweet
import com.github.andrewsurratt.twitter.repository.TweetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam
import java.security.Principal
import java.util.*

@RestController
public class TweetController {
	@Autowired
	private lateinit var tweetRepository: TweetRepository;

	@RequestMapping("/tweets")
	fun getTweets(): List<Tweet> {
		return tweetRepository.findAll();
	}

	@RequestMapping("/tweet/create")
	fun getTweets(
		@RequestParam(name = "tweet") tweet: String,
		principal: Principal,
	): List<Tweet> {
		return listOf(tweetRepository.save(
			Tweet(
			principal.name,
			tweet
		)
		));
	}

}
