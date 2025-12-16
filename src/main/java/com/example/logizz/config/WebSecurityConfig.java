package com.example.logizz.config;

import com.example.logizz.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserService userService;

    public WebSecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userService).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http
            
            // (optional for now) if you are using only Thymeleaf forms, keep CSRF enabled.
            // If you use fetch/REST with POST/PUT/DELETE, you’ll need CSRF tokens or disable it.
            .authorizeHttpRequests(auth -> auth
                // public pages + login processing
.requestMatchers("/", "/index", "/register", "/login", "/error",
        "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                // allow normal logged-in users
                .requestMatchers("/quiz", "/quiz/submit", "/result", "/profile").authenticated()

                // admin pages
                .requestMatchers("/quizList/**", "/addQuiz", "/editQuiz/**").hasRole("ADMIN")

                // REST API (optional rules)
                .requestMatchers("/api/register").permitAll()
                .requestMatchers("/api/questions", "/api/submit").authenticated()
                .requestMatchers("/api/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/index")
                .loginProcessingUrl("/login")   // IMPORTANT: matches your form action
                .defaultSuccessUrl("/profile", true)
                .failureUrl("/index?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index?logout=true")
                .permitAll()
            );

        return http.build();
    }
}
