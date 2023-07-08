package com.github.andrewsurratt.twitter.controller

import com.github.andrewsurratt.twitter.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping
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

    @PostMapping("/user")
    fun registerUser(req: HttpServletRequest): Map<String, String> {
        val username = req.userPrincipal.name
        userRepository.save(com.github.andrewsurratt.twitter.entity.User(
            username,
            "Test",
            "User"
        ))
        return Collections.singletonMap("name", username);
    }
}
