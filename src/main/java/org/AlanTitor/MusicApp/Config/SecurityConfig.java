package org.AlanTitor.MusicApp.Config;

import lombok.AllArgsConstructor;
import org.AlanTitor.MusicApp.Entity.Users.Role;
import org.AlanTitor.MusicApp.Jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(c ->
            c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(c -> { c
                        .requestMatchers(HttpMethod.POST,"/api/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/users/refresh").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/users").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET,"/api/users/{id}").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET,"/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/v3/api-docs").permitAll()
                        .anyRequest().authenticated();
                })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c -> {
                    c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

                    c.accessDeniedHandler(((request, response, accessDeniedException) ->
                            response.setStatus(HttpStatus.FORBIDDEN.value())));
                });

        return http.build();
    }
}
