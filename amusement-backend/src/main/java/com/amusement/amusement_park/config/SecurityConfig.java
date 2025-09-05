package com.amusement.amusement_park.config;

import com.amusement.amusement_park.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Allow OPTIONS requests for CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints - no authentication required
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/home").permitAll()

                        // User registration and authentication endpoints
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/verify-otp").permitAll()
                        .requestMatchers("/api/users/resend-otp").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/auth/forgot-password").permitAll()
                        .requestMatchers("/auth/reset-password").permitAll()

                        // Public API endpoints
                        .requestMatchers("/api/membership-plans").permitAll()
                        .requestMatchers("/api/rides").permitAll()
                        .requestMatchers("/api/ticket-types").permitAll()

                        // Ticket endpoints with role-based access
                        .requestMatchers(HttpMethod.GET, "/api/tickets/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/tickets/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/tickets/**").permitAll()

                        // Ticket types endpoints
                        .requestMatchers(HttpMethod.GET, "/api/ticket-types/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/ticket-types/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/ticket-types/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/ticket-types/**").permitAll()

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").permitAll()

                        // User memberships
                        .requestMatchers("/api/user-memberships/**").permitAll()

                        // Profile endpoints - require authentication
                        .requestMatchers("/api/profile/**").permitAll()

                        // All other requests require authentication
                        .anyRequest().permitAll())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}