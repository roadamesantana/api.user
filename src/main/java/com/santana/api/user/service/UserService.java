package com.santana.api.user.service;

import com.santana.api.user.data.UserRepository;
import com.santana.api.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Flux<User> getUsers(long limit) {
        return userRepository.findAll().take(limit);
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Mono<User> newUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    public Mono<Void> deleteUser(@PathVariable("id") String id) {
        return userRepository.deleteById(id);
    }

    public Mono<User> findUserByExample(User user) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase()
                .withMatcher("email", GenericPropertyMatcher::contains)
                .withMatcher("role", GenericPropertyMatcher::exact);
        Example<User> example = Example.of(user, matcher);

        return userRepository.findOne(example);
    }

}
