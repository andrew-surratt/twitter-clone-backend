package com.github.andrewsurratt.twitter

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import javax.sql.DataSource


@Configuration
@EnableWebSecurity
class Configuration {
    private val log: Log = LogFactory.getLog(com.github.andrewsurratt.twitter.Configuration::class.java.name)

    @Value("spring.datasource.url")
    var springDatasourceUrl: String = "";

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authorization ->
                authorization
                    .anyRequest().authenticated()
            }
            .httpBasic(withDefaults())
            .formLogin(withDefaults())
        log.info("filterChain")
        return http.build()
    }

    @Bean
    fun dataSource(): DataSource {
        log.info("Spring datasource ${springDatasourceUrl}")
        return DataSourceBuilder.create().url("jdbc:postgresql://localhost:5432/postgres").build()
    }

    @Bean
    fun users(dataSource: DataSource): UserDetailsManager? {
        log.info("Create users")
        val userManager = JdbcUserDetailsManager(dataSource)
        // Demo Users
        val username1 = "testuser"
        if (!userManager.userExists(username1)) {
            val user: UserDetails = User.withDefaultPasswordEncoder()
                .username(username1)
                .password("password")
                .roles("USER")
                .build()
            userManager.createUser(user)
        }
        return userManager
    }
}
