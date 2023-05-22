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

/**
 * Controller to handle admin related requests
 * Secured to ROLE_ADMIN only
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class AdminController {

    /**
     * Service to perform user related operations
     */
    private final UserService userService;
    /**
     * Repository to perform event related operations
     */
    public final EventsRepositiry eventsRepositiry;

    /**
     * Handles request to the admin page
     * @param model to add attributes
     * @param authentication to get current user
     * @return view name for admin page
     * @throws AccessDeniedException if the user is not an admin
     */
    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRoles().contains(Role.ROLE_ADMIN)) {
            model.addAttribute("users", userService.list());
            return "admin";
        }

        throw new AccessDeniedException("Forbidden");
    }

    /**
     * Bans a user by ID
     * @param id of the user to ban
     * @return the redirect view admin
     */
    @PostMapping("/admin/user/ban/{id}")
    public String userBan(@PathVariable("id") Long id) {
        userService.banUser(id);
        return "redirect:/admin";
    }

    /**
     * Deletes a user by ID
     * @param id of the user to delete
     * @return the redirect view admin
     */
    @PostMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    /**
     * Shows details for a user
     * @param id of the user
     * @param model to add attributes
     * @return view name for user-details page
     */
    @GetMapping("/admin/user/details/{id}")
    public String userDetails(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("events", eventsRepositiry.findEventsByOwner(user));
        return "user-details";
    }

    /**
     * Shows the access denied page
     * @return view name for access-denied page
     */
    @GetMapping("/access-denied")
    public String accessDenied(){
        return "access-denied";
    }

    /**
     * Handles access denied exception and shows the access denied page
     * @return view name for access-denied page
     */
    @ExceptionHandler(AccessDeniedException.class)
    public String accessDeniedHandler() {
        return "access-denied";
    }
}
