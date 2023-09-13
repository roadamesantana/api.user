package com.santana.api.user.controller;

import com.santana.api.user.domain.User;
import com.santana.api.user.dto.AuthRequest;
import com.santana.api.user.dto.AuthResponse;
import com.santana.api.user.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuthRestController {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReactiveUserDetailsService userDetailsService;

    @PostMapping("/auth")
    public Mono<ResponseEntity<AuthResponse>> auth(@RequestBody AuthRequest ar) {
        return userDetailsService
                .findByUsername(ar.getEmail())
                .map((userDetails) -> {
                    if (passwordEncoder.matches(ar.getPassword(), userDetails.getPassword())) {
                        return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken((User) userDetails)));
                    } else {
                        return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .build();
                    }
                });
    }

}
