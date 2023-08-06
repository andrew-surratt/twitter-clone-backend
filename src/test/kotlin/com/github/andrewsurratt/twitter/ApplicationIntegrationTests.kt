package com.github.andrewsurratt.twitter

import com.github.andrewsurratt.twitter.entity.User
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(profiles = ["test"])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationIntegrationTests {

	@LocalServerPort
	private lateinit var port: String

	@Autowired
	private lateinit var restTemplate: TestRestTemplate

	@Autowired
	private lateinit var configurationProperties: ConfigurationProperties

	private lateinit var endUserHeaders: HttpHeaders

	private lateinit var endUserConfig: ConfigurationProperties.UserConfig

	private lateinit var endUser: User

	private lateinit var userControllerUrl: String

	private lateinit var tweetControllerUrl: String

	private fun createURLWithPort(path: String = "/user"): String {
		return "http://localhost:$port$path"
	}

	@BeforeAll
	fun init() {
		this.userControllerUrl = createURLWithPort("/user")
		this.tweetControllerUrl = createURLWithPort("/tweet")
		this.endUserConfig = configurationProperties.users.find { u -> u.role == "USER" }!!
		this.endUser = User(
				endUserConfig.name,
		"testFirstname",
		"testLastname"
		)
		this.endUserHeaders = HttpHeaders()
		this.endUserHeaders.contentType = MediaType.APPLICATION_JSON
		this.endUserHeaders.accept = listOf(MediaType.APPLICATION_JSON)
		this.endUserHeaders.setBasicAuth(
			endUserConfig.name,
			endUserConfig.password
		)
	}

	@Test
	fun contextLoads() {
		// Should load application
	}

	@Test
	fun testUserLifecycle() {
		testRegisterUser(endUser)
		testGetUser(endUser, HttpStatusCode.valueOf(200))
		testDeleteUser(HttpStatusCode.valueOf(200))
		testGetUser(null, HttpStatusCode.valueOf(404))
	}

	@Test
	fun testGetMissingUser() {
		testGetUser(null, HttpStatusCode.valueOf(404))
	}

	@Test
	fun testDeleteMissingUser() {
		testDeleteUser(HttpStatusCode.valueOf(404))
	}

	@Test
	fun testTweetLifecycle() {
		testRegisterUser(endUser)

		val expectedTweet = "test tweet text"
		val entity = HttpEntity<String>("{\"tweetText\":\"$expectedTweet\"}}", endUserHeaders)
		val response: ResponseEntity<String> = this.restTemplate.exchange(
			tweetControllerUrl,
			HttpMethod.POST,
			entity,
			String::class.java
		)

		assertEquals(HttpStatusCode.valueOf(200), response.statusCode)
		assertNotNull(response.body)
		assertJsonResponse(response.body!!, expectedTweet, "tweet")
		val tweetIdResponse = getTweetIdFromJson(response.body!!)
		assertNotNull(tweetIdResponse)

		testDeleteTweet(tweetIdResponse!!)
		testDeleteUser()
	}

	private fun testDeleteTweet(tweetId: String) {
		val entity = HttpEntity<String>("{\"tweetId\":\"$tweetId\"}}", endUserHeaders)
		val response: ResponseEntity<String> = this.restTemplate.exchange(
			tweetControllerUrl,
			HttpMethod.DELETE,
			entity,
			String::class.java
		)
		assertEquals(HttpStatusCode.valueOf(200), response.statusCode)
	}

	private fun getTweetIdFromJson(responseBody: String) =
		Regex("(\"tweetId\":\")(.{36})").find(responseBody.toString())?.groupValues?.get(2)

	private fun testRegisterUser(user: User) {
		val entity = HttpEntity<String>(
			"{\"firstname\":\"${user.firstname}\",\"lastname\":\"${user.lastname}\"}",
			endUserHeaders
		)
		val response: ResponseEntity<String> = this.restTemplate.exchange(
			userControllerUrl,
			HttpMethod.POST,
			entity,
			String::class.java
		)
		assertEquals(HttpStatusCode.valueOf(200), response.statusCode)
		assertNotNull(response.body)
		val responseBody = response.body!!
		assertUserResponse(responseBody, user)
	}

	private fun testGetUser(expectedUser: User?, expectedResponseCode: HttpStatusCode = HttpStatusCode.valueOf(200)) {
		val entity = HttpEntity<String>(null, endUserHeaders)
		val response: ResponseEntity<String> = this.restTemplate.exchange(
			userControllerUrl,
			HttpMethod.GET,
			entity,
			String::class.java
		)

		assertEquals(expectedResponseCode, response.statusCode)

		assertUserResponse(response.body, expectedUser)
	}

	private fun testDeleteUser(expectedResponseCode: HttpStatusCode = HttpStatusCode.valueOf(200)) {
		val entity = HttpEntity<String>(null, endUserHeaders)
		val response: ResponseEntity<String> = this.restTemplate.exchange(
			userControllerUrl,
			HttpMethod.DELETE,
			entity,
			String::class.java
		)

		assertEquals(expectedResponseCode, response.statusCode)
		assertEquals(null, response.body)
	}

	private fun assertUserResponse(responseBody: String?, user: User?) {
		if (user == null || responseBody == null) {
			assertNull(responseBody)
			assertNull(user)
		} else {
			assertJsonResponse(responseBody, user.username, "username")
			assertJsonResponse(responseBody, user.firstname, "firstname")
			assertJsonResponse(responseBody, user.lastname, "lastname")
		}
	}

	private fun assertJsonResponse(
		responseBody: String,
		expected: String,
		property: String
	) {
		assertThat(responseBody, containsString("\"$property\":\"$expected\""))
	}
}
