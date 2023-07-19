package com.github.andrewsurratt.twitter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan("com.github.andrewsurratt.*")
@EnableJpaRepositories
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
