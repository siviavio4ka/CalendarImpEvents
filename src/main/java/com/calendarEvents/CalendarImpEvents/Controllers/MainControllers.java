package com.calendarEvents.CalendarImpEvents.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for processing requests to the main page of the application
 */
@Controller
public class MainControllers {

    /**
     * Handles the request to the home page
     * @param model to add attributes
     * @return the view name for the home page
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Главная страница");
        return "home";
    }

}
