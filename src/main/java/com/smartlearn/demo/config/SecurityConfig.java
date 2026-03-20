package com.smartlearn.demo.config;

import com.smartlearn.demo.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth

                // ══ PREFLIGHT CORS - toujours autorisé ══
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ══ AUTH - public ══
                .requestMatchers("/api/auth/**").permitAll()

                // ══ SWAGGER / ACTUATOR - public ══
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                 "/swagger-ui.html", "/actuator/**").permitAll()

                // ══ CATEGORIES - lecture publique, écriture admin ══
                .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/categories/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").authenticated()

                // ══ COURSES - catalogue public, reste authentifié ══
                .requestMatchers(HttpMethod.GET, "/api/courses/published/all").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/courses/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/courses/category/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/courses/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/courses/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/courses/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/courses/**").authenticated()

                // ══ MODULES - authentifié ══
                .requestMatchers("/api/modules/**").authenticated()

                // ══ LESSONS - authentifié ══
                .requestMatchers("/api/lessons/**").authenticated()

                // ══ ENROLLMENTS - authentifié ══
                .requestMatchers("/api/enrollments/**").authenticated()

                // ══ PROGRESS - authentifié ══
                .requestMatchers("/api/progress/**").authenticated()

                // ══ QUIZZES - authentifié ══
                .requestMatchers("/api/quizzes/**").authenticated()

                // ══ QUIZ ATTEMPTS - authentifié ══
                .requestMatchers("/api/quiz-attempts/**").authenticated()

                // ══ PAYMENTS - authentifié ══
                .requestMatchers("/api/payments/**").authenticated()

                // ══ STRIPE - authentifié ══
                .requestMatchers("/api/stripe/**").authenticated()

                // ══ ADMIN - authentifié (le @PreAuthorize gère le rôle) ══
                .requestMatchers("/api/admin/**").authenticated()

                // ══ TOUT LE RESTE - authentifié ══
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}