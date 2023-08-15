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

	private lateinit var replyControllerUrl: String

	private fun createURLWithPort(path: String = "/user"): String {
		return "http://localhost:$port$path"
	}

	@BeforeAll
	fun init() {
		this.userControllerUrl = createURLWithPort("/user")
		this.tweetControllerUrl = createURLWithPort("/tweet")
		this.replyControllerUrl = createURLWithPort("/reply")
		this.endUserConfig = configurationProperties.users.find { u -> u.role == "USER" }!!
		this.endUser = User(
				endUserConfig.name,
			"testFirstname",
			"testLastname",
			"testProfilePictureUrl"
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
		val patchedUser = User(
			endUser.username,
			endUser.firstname,
			endUser.lastname,
			"testPatchedUrl"
		)
		testPatchUser(patchedUser)
		testGetUser(patchedUser, HttpStatusCode.valueOf(200))
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
		val tweetIdResponse = testCreateTweet(expectedTweet)
		assertNotNull(tweetIdResponse)
		testGetTweet(tweetIdResponse!!, HttpStatusCode.valueOf(200), expectedTweet)
		val replyText = "test reply text"
		val replyId = testCreateReply(tweetIdResponse, replyText)
		assertNotNull(replyId)
		testGetReply(replyId!!, HttpStatusCode.valueOf(200), replyText)

		testDeleteTweet(tweetIdResponse)
		testGetTweet(tweetIdResponse, HttpStatusCode.valueOf(404), null)
		testGetReply(replyId, HttpStatusCode.valueOf(404), null)
		testDeleteUser()
	}

	private fun testCreateReply(tweetId: String, replyText: String): String? {
		val entity = HttpEntity<String>("{\"tweetId\":\"$tweetId\",\"replyText\":\"$replyText\"}", endUserHeaders)
		val response: ResponseEntity<String> = this.restTemplate.exchange(
			replyControllerUrl,
			HttpMethod.POST,
			entity,
			String::class.java
		)

		assertEquals(HttpStatusCode.valueOf(200), response.statusCode)
		assertNotNull(response.body)
		assertJsonResponse(response.body!!, replyText, "reply")
		assertJsonResponse(response.body!!, endUser.username, "username")
		val replyId = getUUIDPropertyFromJson("replyId", response.body!!)
		assertNotNull(replyId)
		return replyId
	}

	private fun testGetReply(
		replyId: String,
		expectedResponseCode: HttpStatusCode = HttpStatusCode.valueOf(200),
		expectedResponse: String?
	) {
		val entity = HttpEntity<String>(endUserHeaders)
		val response: ResponseEntity<String> = this.restTemplate.exchange(
			"$replyControllerUrl/$replyId",
			HttpMethod.GET,
			entity,
			String::class.java
		)

		assertEquals(expectedResponseCode, response.statusCode)
		if (expectedResponse != null) {
			assertNotNull(response.body)
			assertJsonResponse(response.body!!, expectedResponse, "reply")
		}
	}

	private fun testCreateTweet(expectedTweet: String): String? {
		val entity = HttpEntity<String>("{\"tweetText\":\"$expectedTweet\"}", endUserHeaders)
		val response: ResponseEntity<String> = this.restTemplate.exchange(
			tweetControllerUrl,
			HttpMethod.POST,
			entity,
			String::class.java
		)

		assertEquals(HttpStatusCode.valueOf(200), response.statusCode)
		assertNotNull(response.body)
		assertJsonResponse(response.body!!, expectedTweet, "tweet")
		assertJsonResponse(response.body!!, endUser.username, "username")
		val tweetIdResponse = getUUIDPropertyFromJson("tweetId", response.body!!)
		assertNotNull(tweetIdResponse)
		return tweetIdResponse
	}

	private fun testGetTweet(
		tweetId: String,
		expectedResponseCode: HttpStatusCode = HttpStatusCode.valueOf(200),
		expectedResponse: String?
	) {
		val entity = HttpEntity<String>(endUserHeaders)
		val response: ResponseEntity<String> = this.restTemplate.exchange(
			"$tweetControllerUrl/$tweetId",
			HttpMethod.GET,
			entity,
			String::class.java
		)

		assertEquals(expectedResponseCode, response.statusCode)
		if (expectedResponse != null) {
			assertNotNull(response.body)
			assertJsonResponse(response.body!!, expectedResponse, "tweet")
		}
	}

	private fun testDeleteTweet(tweetId: String) {
		val entity = HttpEntity<String>(endUserHeaders)
		val response: ResponseEntity<String> = this.restTemplate.exchange(
			"$tweetControllerUrl/$tweetId",
			HttpMethod.DELETE,
			entity,
			String::class.java
		)
		assertEquals(HttpStatusCode.valueOf(200), response.statusCode)
	}

	private fun getUUIDPropertyFromJson(property: String, responseBody: String): String? =
		Regex("(\"$property\":\")(.{36})").find(responseBody)?.groupValues?.get(2)

	private fun testRegisterUser(user: User) {
		val entity = HttpEntity<String>(
			"{\"firstname\":\"${user.firstname}\",\"lastname\":\"${user.lastname}\",\"profilePictureUrl\":\"${user.profilePictureUrl}\"}",
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

	private fun testPatchUser(patchedUser: User) {
		val entity = HttpEntity<String>("{\"profilePictureUrl\":\"${patchedUser.profilePictureUrl}\"}", endUserHeaders)
		val response: ResponseEntity<String> = this.restTemplate.exchange(
			userControllerUrl,
			HttpMethod.PATCH,
			entity,
			String::class.java
		)

		assertEquals(HttpStatusCode.valueOf(200), response.statusCode)

		assertUserResponse(response.body, patchedUser)
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
			assertJsonResponse(responseBody, user.profilePictureUrl, "profilePictureUrl")
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
