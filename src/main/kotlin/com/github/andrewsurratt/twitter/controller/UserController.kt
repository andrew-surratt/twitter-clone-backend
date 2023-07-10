package com.github.andrewsurratt.twitter.controller

import com.github.andrewsurratt.twitter.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.*
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.Collections;

@RestController
class UserController {
    @Autowired
    private lateinit var userRepository: UserRepository;

    @GetMapping("/user")
    fun getUser(req: HttpServletRequest): Map<String, String> {
        val user = userRepository.findDistinctFirstByUsername(req.userPrincipal.name)
        return Collections.singletonMap("name", "${user.firstname} ${user.lastname}");
    }

    @PostMapping(
        value = ["/user"],
        produces = [APPLICATION_JSON_VALUE],
        consumes = [APPLICATION_JSON_VALUE]
    )
    fun registerUser(
        req: HttpServletRequest,
        @RequestBody body: CreateUserRequestBody
    ): Map<String, String> {
        val username = req.userPrincipal.name
        userRepository.save(
            com.github.andrewsurratt.twitter.entity.User(
                username,
                body.firstname,
                body.lastname
            )
        )
        return Collections.singletonMap("name", username);
    }

    public class CreateUserRequestBody(val firstname: String, val lastname: String)
}
