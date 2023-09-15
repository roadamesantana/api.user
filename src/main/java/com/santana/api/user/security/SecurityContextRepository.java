package com.santana.api.user.security;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository{

    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange swe) {
        ServerHttpRequest request = swe.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);

            Claims claims = jwtUtil.getAllClaimsFromToken(authToken);
            String role = claims.get("role", String.class);

            Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken, Arrays.asList(new SimpleGrantedAuthority(role)));
            return this.authenticationManager.authenticate(auth).map((authentication) -> {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return new SecurityContextImpl(authentication);
            });
        } else {
            return Mono.empty();
        }
    }
}
