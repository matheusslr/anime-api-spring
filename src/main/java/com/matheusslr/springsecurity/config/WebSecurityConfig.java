package com.matheusslr.springsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();

        return http.csrf().disable()
                .httpBasic()
                .and()
                .authorizeHttpRequests(
                        auth -> {
                            auth.requestMatchers(HttpMethod.GET, "/h2-console/**").hasRole("ADMIN");
                            auth.requestMatchers(HttpMethod.POST, "/auth/register").hasAnyRole("ADMIN", "MANAGER");
                            auth.requestMatchers(HttpMethod.DELETE, "/animes/**").hasRole("ADMIN");
                            auth.anyRequest().authenticated();
                        })
                .formLogin(Customizer.withDefaults())
                .build();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
