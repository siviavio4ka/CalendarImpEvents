package com.calendarEvents.CalendarImpEvents.Config.Configurations;

import com.calendarEvents.CalendarImpEvents.Config.Handlers.MyAuthenticationSuccessHandler;
import com.calendarEvents.CalendarImpEvents.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

import static com.calendarEvents.CalendarImpEvents.models.Enams.Role.ROLE_ADMIN;

/**
 * Security configuration class.
 * Secures application URLs and configures Spring Security.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Service for retrieving user data
     */
    private final UserService userService;

    /**
     * Defines a BCryptPasswordEncoder bean
     * @return the BCryptPasswordEncoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Injects the authentication success handler
     */
    @Autowired
    MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    /**
     * Method that exposes the authentication success handler bean
     * @return the authentication success handler
     */
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return myAuthenticationSuccessHandler;
    }

    /**
     * Method that configures security filters
     * @param http the HttpSecurity
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while setting filters
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //.authorizeRequests()
                .authorizeHttpRequests()
                .requestMatchers("/login","/register")
                .permitAll()
                .requestMatchers("/admin/**")
                .hasAuthority(ROLE_ADMIN.getAuthority())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler())
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new AccessDeniedHandler() {
                    /**
                     * Handles access denied exception by redirecting to the access denied page
                     * @param request the HTTP request
                     * @param response the HTTP response
                     * @param accessDeniedException the AccessDeniedException
                     * @throws IOException if an I/O error occurs
                     * @throws ServletException  if a servlet exception occurs
                     */
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        response.sendRedirect("/access-denied");
                    }
                })
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
        return http.build();
    }
}
