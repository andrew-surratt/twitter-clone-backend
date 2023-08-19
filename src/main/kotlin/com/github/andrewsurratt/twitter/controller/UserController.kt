package com.github.andrewsurratt.twitter.controller

import com.github.andrewsurratt.twitter.entity.User
import com.github.andrewsurratt.twitter.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(originPatterns = ["http://localhost*"])
class UserController {
    @Autowired
    private lateinit var userRepository: UserRepository;

    @GetMapping("/user")
    fun getUser(req: HttpServletRequest): ResponseEntity<User> {
        val user = userRepository.findDistinctFirstByUsername(req.userPrincipal.name)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user);
    }

    @PostMapping(
        value = ["/user"],
        produces = [APPLICATION_JSON_VALUE],
        consumes = [APPLICATION_JSON_VALUE]
    )
    fun registerUser(
        req: HttpServletRequest,
        @RequestBody body: CreateUserRequestBody
    ): ResponseEntity<User> {
        val username = req.userPrincipal.name
        if (userRepository.findDistinctFirstByUsername(username) != null) {
            return ResponseEntity.badRequest().build()
        }
        val user = User(
            username,
            body.firstname ?: "",
            body.lastname ?: "",
            body.profilePictureUrl ?: ""
        )
        userRepository.save(user)
        return ResponseEntity.ok(user)
    }

    @PatchMapping(
        value = ["/user"],
        produces = [APPLICATION_JSON_VALUE],
        consumes = [APPLICATION_JSON_VALUE]
    )
    fun patchUser(
        req: HttpServletRequest,
        @RequestBody body: PatchUserRequestBody
    ): ResponseEntity<User> {
        val user = userRepository.findDistinctFirstByUsername(req.userPrincipal.name)
            ?: return ResponseEntity.notFound().build()
        if (body.firstname != null) {
            user.firstname = body.firstname
        }
        if (body.lastname != null) {
            user.lastname = body.lastname
        }
        if (body.profilePictureUrl != null) {
            user.profilePictureUrl = body.profilePictureUrl
        }
        userRepository.save(user)
        return ResponseEntity.ok(user)
    }

    @DeleteMapping(
        value = ["/user"],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun deleteUser(
        req: HttpServletRequest,
    ): ResponseEntity<Unit> {
        val user = userRepository.findDistinctFirstByUsername(req.userPrincipal.name)
                ?: return ResponseEntity.notFound().build()
        userRepository.delete(user)
        return ResponseEntity.ok().build()
    }

    class CreateUserRequestBody(
        val firstname: String?,
        val lastname: String?,
        val profilePictureUrl: String?
    )

    class PatchUserRequestBody(
        val firstname: String?,
        val lastname: String?,
        val profilePictureUrl: String?
    )
}
