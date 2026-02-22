package com.nishant.AuthKit.config;

import com.nishant.AuthKit.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        // 1. Disable CSRF (Stateless APIs don't need session protection)
                        .csrf(csrf -> csrf.disable())

                        // 2. Configure CORS (If your frontend is on a different port/domain)
                        .cors(cors -> cors.configure(http))

                        // 3. Authorization Rules
                        .authorizeHttpRequests(auth -> auth
                                // Public endpoints (White-listing)
                                // It is best practice to group them, e.g., /api/v1/auth/**
                                .requestMatchers(
                                        "/api/v1/auth/**",
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html"
                                ).permitAll()

                                // Secure all other endpoints
                                .anyRequest().authenticated()
                        )

                        // 4. Session Management
                        // STATELESS means Spring Security will never create an HttpSession
                        // and it will never use it to obtain the SecurityContext.
                        .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )

                        // 5. Set the custom AuthenticationProvider (DaoAuthenticationProvider usually)
                        .authenticationProvider(authenticationProvider)

                        // 6. Add JWT Filter
                        // We add it BEFORE UsernamePasswordAuthenticationFilter because:
                        // - UsernamePasswordAuthFilter is the default filter that handles form logins.
                        // - We want to check for a JWT token *before* Spring tries to check for username/password form data.
                        // - If the JWT filter finds a valid token, it populates the SecurityContext,
                        //   and the UsernamePasswordAuthFilter sees the user is already authenticated and skips its logic.
                        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                // 7. (Optional) Logout configuration
                // .logout(logout -> logout
                //     .logoutUrl("/api/v1/auth/logout")
                //     .addLogoutHandler(logoutHandler)
                //     .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                // );

                return http.build();
        }
}