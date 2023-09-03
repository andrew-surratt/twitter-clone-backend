package com.github.andrewsurratt.twitter.services

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.andrewsurratt.twitter.ConfigurationProperties
import kotlinx.coroutines.runBlocking
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.io.IOException
import java.net.URLEncoder


@Service
class GoogleFactCheckService {
    private val log: Log = LogFactory.getLog(GoogleFactCheckService::class.java.name)

    private var client: WebClient = WebClient.create("https://factchecktools.googleapis.com");

    @Autowired
    lateinit var configurationProperties: ConfigurationProperties

    fun checkClaim(query: String): ClaimResponse?  {
        val key = configurationProperties.api.googleFactCheck.apiKey
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        log.info("[checkClaim] Sending encoded query $encodedQuery")
        return runBlocking {
            client.get()
                .uri("/v1alpha1/claims:search?languageCode=en&pageSize=3&query={query}&key={key}", query, key)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().awaitBody<ClaimResponse>()
        }
    }

    @JsonDeserialize(using = ClaimReviewDeserializer::class)
    data class ClaimReview(
        var url: String = "",
        var title: String = "",
        var reviewDate: String = "",
        var textualRating: String = "",
        var languageCode: String = ""
    )

    @JsonDeserialize(using = ClaimDeserializer::class)
    data class Claim(
        var text: String = "",
        var claimant: String = "",
        var claimDate: String = "",
        var claimReview: List<ClaimReview> = emptyList()
    )

    @JsonDeserialize(using = ClaimResponseDeserializer::class)
    data class ClaimResponse(
        var claims: List<Claim> = emptyList(),
        var nextPageToken: String = ""
    )


    class ClaimResponseDeserializer : JsonDeserializer<ClaimResponse?>() {
        private val log: Log = LogFactory.getLog(ClaimResponseDeserializer::class.java.name)

        @Throws(IOException::class)
        override fun deserialize(jsonParser: JsonParser, ctxt: DeserializationContext?): ClaimResponse {
            val codec: ObjectCodec = jsonParser.codec
            val tree: JsonNode = codec.readTree(jsonParser)
            val objectMapper = ObjectMapper();
            log.info("ClaimResponse: ${tree.toPrettyString()}")
            val claims = tree.get("claims")
            return ClaimResponse(
                objectMapper.readValue(claims?.toString() ?: "[]", Array<Claim>::class.java).asList(),
                tree.get("nextPageToken")?.asText() ?: ""
            )
        }
    }

    class ClaimDeserializer : JsonDeserializer<Claim?>() {
        @Throws(IOException::class)
        override fun deserialize(jsonParser: JsonParser, ctxt: DeserializationContext?): Claim {
            val codec: ObjectCodec = jsonParser.codec
            val tree: JsonNode = codec.readTree(jsonParser)
            val objectMapper = ObjectMapper()
            val claimant = tree.get("claimant")
            return Claim(
                tree.get("text").asText(),
                if (claimant == null) "" else claimant.asText(),
                tree.get("claimDate")?.asText() ?: "",
                objectMapper.readValue(tree.get("claimReview").toString(), Array<ClaimReview>::class.java).asList(),
            )
        }
    }

    class ClaimReviewDeserializer : JsonDeserializer<ClaimReview?>() {
        @Throws(IOException::class)
        override fun deserialize(jsonParser: JsonParser, ctxt: DeserializationContext?): ClaimReview {
            val codec: ObjectCodec = jsonParser.codec
            val tree: JsonNode = codec.readTree(jsonParser)
            return ClaimReview(
                tree.get("url").asText(),
                tree.get("title").asText(),
                tree.get("reviewDate").asText(),
                tree.get("textualRating").asText(),
                tree.get("languageCode").asText(),
            )
        }
    }
}
