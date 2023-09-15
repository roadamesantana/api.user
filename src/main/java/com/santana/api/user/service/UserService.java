package com.santana.api.user.service;

import com.santana.api.user.data.UserRepository;
import com.santana.api.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Flux<User> getUsers(long limit) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (!authentication.isAuthenticated()) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "User not authenticated");
        }

        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADM"))) {
            return userRepository.findByEnabledIsTrue().take(limit);
        }

        String email = (String) authentication.getPrincipal();
        return userRepository.findByEmailContainingIgnoreCaseAndEnabledIsTrue(email).flux();
    }

    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmailContainingIgnoreCaseAndEnabledIsTrue(email);
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Mono<User> newUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setId(null);
        return userRepository.save(user);
    }

    @PreAuthorize("hasAuthority('ADM')")
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
