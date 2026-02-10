package com.soskate.api.config;

import com.soskate.api.security.CustomAccessDeniedHandler;
import com.soskate.api.security.CustomAuthenticationEntryPoint;
import com.soskate.api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth

                        // Public

                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/email-exists").permitAll()
                        .requestMatchers(HttpMethod.POST, "/customer/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/customer/auth/email-exists").permitAll()
                        .requestMatchers(HttpMethod.GET, "/instructors/activate/validate").permitAll()
                        .requestMatchers(HttpMethod.POST, "/instructors/activate").permitAll()
                        .requestMatchers(HttpMethod.GET, "/instructors", "/instructors/{id}", "/instructors/specialty/{specialty}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/spots", "/spots/active", "/spots/{id}", "/spots/city/{city}", "/spots/type", "/spots/nearby", "/spots/{spotId}/instructors").permitAll()
                        .requestMatchers(HttpMethod.GET, "/services", "/services/active", "/services/{id}", "/services/type/{type}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/photos/**").permitAll()

                        // Admin

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/services").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/services/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/services/{id}/deactivate").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/services/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/spots").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/spots/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/spots/{id}/deactivate").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/spots/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/photos/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/photos/cleanup").hasRole("ADMIN")

                        // Instructor

                        .requestMatchers(HttpMethod.PUT, "/instructors/{id}").hasRole("INSTRUCTOR")
                        .requestMatchers("/instructors/{instructorId}/availabilities/**").hasRole("INSTRUCTOR")
                        .requestMatchers("/instructors/{instructorId}/spots/**").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.GET, "/instructors/{instructorId}/bookings").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.POST, "/bookings").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.PATCH, "/bookings/{bookingId}/cancel").hasRole("INSTRUCTOR")

                        // Customer

                        .requestMatchers("/customer/{customerId}").hasRole("CUSTOMER")
                        .requestMatchers("/customers/{customerId}/**").hasRole("CUSTOMER")

                        // Authenticated

                        .anyRequest().authenticated()

                ).exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }
}
