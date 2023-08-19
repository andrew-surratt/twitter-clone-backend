package com.github.andrewsurratt.twitter

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "custom")
class ConfigurationProperties(
    val users: List<UserConfig>,
    val api: ApiConfig
) {
    data class UserConfig(val name: String, val password: String, val role: String)

    data class ApiConfig(val googleFactCheck: GoogleFactCheckConfig)

    data class GoogleFactCheckConfig(val apiKey: String)
}
