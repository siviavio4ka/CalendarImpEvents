package com.calendarEvents.CalendarImpEvents.Controllers;

import com.calendarEvents.CalendarImpEvents.models.Enams.Role;
import com.calendarEvents.CalendarImpEvents.models.Events;
import com.calendarEvents.CalendarImpEvents.models.User;
import com.calendarEvents.CalendarImpEvents.repositories.EventsRepositiry;
import com.calendarEvents.CalendarImpEvents.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller to handle navbar related requests
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class NavbarController {

    /**
     * Repository to perform event related operations
     */
    @Autowired
    private EventsRepositiry eventsRepository;
    /**
     * Repository to perform User related operations
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Shows events for the current user
     * @param model to add attributes
     * @param principal to get current user
     * @return view name for events page
     */
    @GetMapping("/events")
    public String events(Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        Iterable<Events> events;
        if (currentUser.hasRole(String.valueOf(Role.ROLE_ADMIN))) {
            events = eventsRepository.findAll();
        } else {
            events = eventsRepository.findEventsByOwner(currentUser);
        }
        model.addAttribute("events", events);
        return "events";
    }

    /**
     * Shows events for a given date
     * @param date the event date
     * @param model to add attributes
     * @return view name for events-data-add page
     */
    @GetMapping("/events/{date}")
    public String getEventsByDate(@PathVariable String date, Model model) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        List<Events> event = eventsRepository.findByDate(localDate);
        model.addAttribute("localDate", localDate);
        model.addAttribute("event", event);
        return "events-data-add";
    }

    /**
     * Adds a new event for a given date
     * @param date the event date
     * @param title the event title
     * @param description the event description
     * @param model to add attributes
     * @param principal to get current user
     * @return the redirect view events
     */
    @PostMapping("/events/{date}")
    public String eventsAdd(@PathVariable String date, @RequestParam String title, @RequestParam String description, Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        Events event = new Events(title, localDate, description);
        event.setOwner(currentUser);
        eventsRepository.save(event);
        return "redirect:/events";
    }

    /**
     * Shows details for an event
     * @param id of the event
     * @param model to add attributes
     * @param principal to get current user
     * @return view name for events-details page
     */
    @GetMapping("/event/{id}")
    public String eventsDetails(@PathVariable Long id, Model model, Principal principal) {
        Events event = eventsRepository.findById(id).orElseThrow();
        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        checkUserAccess(event, currentUser);
        model.addAttribute("events", event);
        return "events-details";
    }

    /**
     * Shows form to edit an event
     * @param id of the event
     * @param model to add attributes
     * @param principal to get current user
     * @return view name for events-edit page
     */
    @GetMapping("/events/{id}/edit")
    public String eventsEdit(@PathVariable Long id, Model model, Principal principal) {
        Events event = eventsRepository.findById(id).orElseThrow();
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        checkUserAccess(event, currentUser);
        model.addAttribute("events", event);
        return "events-edit";
    }

    /**
     * Updates an event
     * @param id of the event
     * @param title the event title
     * @param description the event description
     * @param model to add attributes
     * @param principal to get current user
     * @return the redirect view events
     */
    @PostMapping("/events/{id}/edit")
    public String eventsUpdate(@PathVariable Long id, @RequestParam String title,@RequestParam String description, Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        Events event = eventsRepository.findById(id).orElseThrow(); // .orElseThrow() исключение если запись не найдена
        checkUserAccess(event, currentUser);
        event.setTitle(title);
        event.setDescription(description);
        eventsRepository.save(event);
        return "redirect:/events";
    }

    /**
     * Deletes an event
     * @param id of the event
     * @param model to add attributes
     * @param principal to get current user
     * @return the redirect view events
     */
    @PostMapping("/events/{id}/remove")
    public String eventsDelete(@PathVariable Long id, Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        Events event = eventsRepository.findById(id).orElseThrow(); // .orElseThrow() исключение если запись не найдена
        checkUserAccess(event, currentUser);
        eventsRepository.delete(event);
        return "redirect:/events";
    }

    /**
     * Handles request to search for events
     * @param query the search query
     * @param model to add attributes
     * @return view name for events-search page
     */
    @GetMapping("/events/search")
    public String eventsFind(@RequestParam String query, Model model) {
        ArrayList<Events> result = (ArrayList<Events>) eventsRepository.findByTitleContainingIgnoreCase(query);
        model.addAttribute("error", result.isEmpty());
        model.addAttribute("result", result);
        return "events-search";
    }

    /**
     * Shows the contacts page
     * @param model to add attributes
     * @return view name for contacts page
     */
    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("title", "Связь с нами");
        return "contacts";
    }

    /**
     * Checks access for an event
     * @param event the Event
     * @param currentUser the current user
     * @throws AccessDeniedException if
     * Current user is not the event owner
     * Current user does not have an admin role
     */
    private void checkUserAccess(Events event, User currentUser) {
        if (!currentUser.equals(event.getOwner()) && !currentUser.hasRole(Role.ROLE_ADMIN.name())) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
