package ru.otus.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authorize ->
                authorize.anyRequest().authenticated()
            )
            .httpBasic()
            .and()
            .logout();
        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        UserDetails user1 = User
            .builder()
            .username("user1")
            .password("$2a$10$U0Mp24ZYVmGU968f9b8.DONhoSSK0pUWsE3K02iIXJBYUppzpH2km") // password1
            .authorities("USER")
            .build();
        UserDetails user2 = User
            .builder()
            .username("user2")
            .password("$2a$10$pau4XBHr8IlDaYVLy1R6je7er37nYK3YW4MirZjFm7Qg87vRYQXhq") // password2
            .authorities("USER")
            .build();
        UserDetails user3 = User
            .builder()
            .username("user3")
            .password("$2a$10$Kxh8kwimeb/q4n9v8xxW0uGeznHwIzN0xQziU0LSqLEqAy/j5oZku") // password3
            .authorities("USER")
            .build();
        UserDetails service = User
            .builder()
            .username("game")
            .password("$2a$10$3DHpnBxoYCmcUl2DtT8wDuMx3AeOZeqcnwsNCHGH40afWo6n.lT2q") // password
            .authorities("GAME")
            .build();
        return new InMemoryUserDetailsManager(user1, user2, user3, service);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
