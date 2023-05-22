package com.calendarEvents.CalendarImpEvents.Controllers;

import com.calendarEvents.CalendarImpEvents.models.Enams.Role;
import com.calendarEvents.CalendarImpEvents.models.User;
import com.calendarEvents.CalendarImpEvents.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller to handle requests related to User management
 */
@Controller
@RequiredArgsConstructor
public class UserController {

    /**
     * Repository for user management
     */
    @Autowired
    private final UserRepository userRepository;
    /**
     * Used to encode passwords
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Handles request to show register page
     * @param model to add attributes
     * @return the view name for the register page
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Logs out the user
     * @param request to get the HTTP session
     * @return the redirect view login
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/login";
    }

    /**
     * Submitting the registration form
     * @param user form data
     * @param bindingResult form validation result
     * @param model to add attributes
     * @return redirect view name on success, "/register" on validation error
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.user", "This name is used");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user.getRoles().add(Role.ROLE_USER);
        userRepository.save(user);
        return "redirect:/login";
    }

    /**
     * Handles request to show login page
     * @return the view name for the login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Handles request to show current user details
     * @param model to add attributes
     * @return view name for home page
     */
    @GetMapping("/user")
    public String user(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("EventsCount", currentUser.getEvents().size());
        return "/";
    }
}
