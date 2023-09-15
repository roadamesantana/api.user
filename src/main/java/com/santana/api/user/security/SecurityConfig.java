package com.santana.api.user.security;

import com.santana.api.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /*@Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }*/

    @Bean
    public OncePerRequestFilter jwtTokenFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                try {
                    // Obtém o token JWT do cabeçalho de autorização
                    String token = extractJwtFromRequest(request);

                    if (token != null) {
                        // Decodifica o token JWT e obtém as informações dele
                        Claims claims = Jwts.parser()
                                .setSigningKey(secret)
                                .parseClaimsJws(token)
                                .getBody();

                        // Você pode adicionar as informações do JWT ao contexto de segurança do Spring Security
                        String subject = claims.getSubject();
                        User principal = new User();
                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(principal, "", new ArrayList<>())
                        );
                    }
                } catch (Exception e) {
                    // Trate erros, se necessário
                }
            }

            private String extractJwtFromRequest(HttpServletRequest request) {
                // Extraia o token JWT do cabeçalho de autorização
                String bearerToken = request.getHeader("Authorization");
                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    return bearerToken.substring(7);
                }
                return null;
            }
        };
    }

    @Bean
    public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http,
                                                          ReactiveAuthenticationManager authenticationManager,
                                                          ServerSecurityContextRepository securityContextRepository) {
        return http
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS)
                .permitAll()
                .pathMatchers("/auth")
                .permitAll()
                .pathMatchers(HttpMethod.DELETE)
                .hasAuthority("ADM")
                .anyExchange()
                .authenticated()
                .and()
                .build();

    }

}
