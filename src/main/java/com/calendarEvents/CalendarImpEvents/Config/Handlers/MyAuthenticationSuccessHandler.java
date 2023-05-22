package com.calendarEvents.CalendarImpEvents.Config.Handlers;

import com.calendarEvents.CalendarImpEvents.models.User;
import com.calendarEvents.CalendarImpEvents.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Handles authentication success by redirecting the user
 */
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Repository for storing and managing users
     */
    @Autowired
    UserRepository userRepository;

    /**
     * Redirects the user based on their role
     * @param req the HTTP request
     * @param res the HTTP response
     * @param auth the Authentication
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws IOException {

        String username = auth.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.isAdmin()) {
                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities()));
                res.sendRedirect("/admin");
            } else {
                // Обновляем Authentication для обычных пользователей
                Authentication auth1 = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth1);
                res.sendRedirect("/");
            }
        }
    }
}