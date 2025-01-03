package com.zerobase.tablereservationservice.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signup","/auth/signin").permitAll()
                        .requestMatchers("/store/register").hasAuthority("ROLE_MANAGER")
                        .requestMatchers("/store/update").hasAuthority("ROLE_MANAGER")
                        .requestMatchers("/store/changemanager").hasAuthority("ROLE_MANAGER")
                        .requestMatchers("/store/delete").hasAuthority("ROLE_MANAGER")
                        .requestMatchers("/store/search").hasAnyAuthority("ROLE_MANAGER", "ROLE_USER")
                        .requestMatchers("/reservation/register").hasAnyAuthority("ROLE_MANAGER", "ROLE_USER")
                        .requestMatchers("/review/register").hasAnyAuthority("ROLE_MANAGER", "ROLE_USER")
                        .requestMatchers("/review/update").hasAnyAuthority("ROLE_MANAGER", "ROLE_USER")
                        .requestMatchers("/review/delete").hasAnyAuthority("ROLE_MANAGER", "ROLE_USER")
                        .requestMatchers("/reservation/approval").hasAuthority("ROLE_MANAGER")
                        .anyRequest().authenticated())
                        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
