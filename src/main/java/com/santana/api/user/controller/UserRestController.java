package com.santana.api.user.controller;

import com.santana.api.user.domain.User;
import com.santana.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/user")
public class UserRestController {

    @Autowired
    UserService userService;

    @GetMapping
    public Flux<User> getUsers(@RequestParam(name = "limit", required = true, defaultValue = "-1") long limit) {
        return userService.getUsers(limit);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable("id") String id) {
        return userService.getUserById(id).map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> newUser(@RequestBody @Valid Mono<User> userMono, ServerHttpRequest req) {
        return userMono.flatMap(user -> {
            return userService.newUser(user)
                    .map(u -> ResponseEntity.created(URI.create(req.getPath() + "/" + u.getId())).build());
        });
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable("id") String id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/search")
    public Mono<User> getUserByExample(@RequestBody User user) {
        return userService.findUserByExample(user);
    }

}
