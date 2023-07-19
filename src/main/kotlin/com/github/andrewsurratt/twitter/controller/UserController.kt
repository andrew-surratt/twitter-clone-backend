package com.github.andrewsurratt.twitter.controller

import com.github.andrewsurratt.twitter.entity.User
import com.github.andrewsurratt.twitter.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.*
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@RestController
class UserController {
    @Autowired
    private lateinit var userRepository: UserRepository;

    @GetMapping("/user")
    fun getUser(req: HttpServletRequest): User {
        return userRepository.findDistinctFirstByUsername(req.userPrincipal.name);
    }

    @PostMapping(
        value = ["/user"],
        produces = [APPLICATION_JSON_VALUE],
        consumes = [APPLICATION_JSON_VALUE]
    )
    fun registerUser(
        req: HttpServletRequest,
        @RequestBody body: CreateUserRequestBody
    ): User {
        val username = req.userPrincipal.name
        val user = User(
            username,
            body.firstname,
            body.lastname
        )
        userRepository.save(user)
        return user;
    }

    class CreateUserRequestBody(val firstname: String, val lastname: String)
}
