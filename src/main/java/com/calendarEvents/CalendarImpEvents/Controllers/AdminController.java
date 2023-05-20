package com.calendarEvents.CalendarImpEvents.Controllers;

import com.calendarEvents.CalendarImpEvents.models.Enams.Role;
import com.calendarEvents.CalendarImpEvents.models.User;
import com.calendarEvents.CalendarImpEvents.repositories.EventsRepositiry;
import com.calendarEvents.CalendarImpEvents.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class AdminController {

    private final UserService userService;
    public final EventsRepositiry eventsRepositiry;

    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRoles().contains(Role.ROLE_ADMIN)) {
            model.addAttribute("users", userService.list());
            return "admin";
        }

        throw new AccessDeniedException("Forbidden");
    }

    @PostMapping("/admin/user/ban/{id}")
    public String userBan(@PathVariable("id") Long id) {
        userService.banUser(id);
        return "redirect:/admin";
    }

    @PostMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/user/details/{id}")
    public String userDetails(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("events", eventsRepositiry.findEventsByOwner(user));
        return "user-details";
    }

    @GetMapping("/access-denied")
    public String accessDenied(){
        return "access-denied";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDeniedHandler() {
        return "access-denied";
    }
}
