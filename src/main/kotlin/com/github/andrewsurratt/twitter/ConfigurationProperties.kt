package com.github.andrewsurratt.twitter

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "custom")
class ConfigurationProperties(val users: List<UserConfig>) {
    data class UserConfig(val name: String, val password: String, val role: String)
}
