package com.santana.api.user.data;

import com.santana.api.user.domain.User;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Mono<User> findByEmailContainingIgnoreCaseAndEnabledIsTrue(String email);

    Flux<User> findByEnabledIsTrue();

    Mono<User> findOne(Example<User> example);
}
