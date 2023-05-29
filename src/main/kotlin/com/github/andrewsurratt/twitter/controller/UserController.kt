package com.github.andrewsurratt.twitter.controller

import org.springframework.boot.actuate.web.exchanges.HttpExchange
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Collections;

@RestController
class UserController {
    @GetMapping("/user")
    fun user(principal: HttpExchange.Principal): Map<String, String> {
        return Collections.singletonMap("name", principal.name);
    }
}
