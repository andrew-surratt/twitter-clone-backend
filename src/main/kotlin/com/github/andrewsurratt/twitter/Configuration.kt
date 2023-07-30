package com.github.andrewsurratt.twitter

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
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

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity, environment: Environment): SecurityFilterChain {
        if (environment["spring.profiles.active"].equals("development")) {
            log.info("Disabling CSRF and CORS for development environment")
            http.csrf(CsrfConfigurer<HttpSecurity>::disable)
                .cors { cors -> cors.disable() }
        }
        http.authorizeHttpRequests { authorization ->
                authorization
                    .requestMatchers(HttpMethod.OPTIONS,"*").permitAll()
                    .anyRequest().authenticated()
            }.httpBasic(Customizer.withDefaults())
        return http.build()
    }

    @Bean
    fun dataSource(environment: Environment): DataSource {
        val springDatasourceUrl = environment["spring.datasource.url"]
        log.info("Spring datasource $springDatasourceUrl")
        return DataSourceBuilder.create().url(springDatasourceUrl).build()
    }

    @Bean
    fun users(dataSource: DataSource): UserDetailsManager? {
        log.info("Create users")
        val userManager = JdbcUserDetailsManager(dataSource)
        // Demo Users
        val user: UserDetails = User.withDefaultPasswordEncoder()
            .username("test")
            .password("password")
            .roles("USER")
            .build()
        val admin: UserDetails = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("password")
            .roles("ADMIN")
            .build()
        val createUsersErrors = createUsers(userManager, listOf(user, admin))
        if (createUsersErrors.isNotEmpty()) {
            log.error(
                "'${createUsersErrors.size}' error(s) while creating users:\n${
                    createUsersErrors.map { error -> error.message }.joinToString("\n")
                }"
            )
        }
        return userManager
    }

    /**
     * Create users
     *
     * @param userManager
     * @param users UserDetails of the users to create
     */
    private fun createUsers(
        userManager: JdbcUserDetailsManager,
        users: List<UserDetails>
    ): List<Throwable> {
        return users.mapNotNull { user ->
            try {
                if (!userManager.userExists(user.username)) {
                    userManager.createUser(user)
                }
                null;
            } catch (error: Throwable) {
                log.error("Error creating user ${user.username}:", error)
                error;
            }
        }
    }
}
