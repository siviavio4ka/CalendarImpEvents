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

@Controller
@Slf4j
@RequiredArgsConstructor
public class NavbarController {

    @Autowired
    private EventsRepositiry eventsRepository;
    @Autowired
    private UserRepository userRepository;


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

    @GetMapping("/events/{date}")
    public String getEventsByDate(@PathVariable String date, Model model) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        List<Events> event = eventsRepository.findByDate(localDate);
        model.addAttribute("localDate", localDate);
        model.addAttribute("event", event);
        return "events-data-add";
    }

    @PostMapping("/events/{date}")
    public String eventsAdd(@PathVariable String date, @RequestParam String title, @RequestParam String description, Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        Events event = new Events(title, localDate, description);
        event.setOwner(currentUser);
        eventsRepository.save(event);
        return "redirect:/events";
    }

    @GetMapping("/event/{id}")
    public String eventsDetails(@PathVariable Long id, Model model, Principal principal) {
        Events event = eventsRepository.findById(id).orElseThrow();
        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        checkUserAccess(event, currentUser);
        model.addAttribute("events", event);
        return "events-details";
    }

    @GetMapping("/events/{id}/edit")
    public String eventsEdit(@PathVariable Long id, Model model, Principal principal) {
        Events event = eventsRepository.findById(id).orElseThrow();
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        checkUserAccess(event, currentUser);
        model.addAttribute("events", event);
        return "events-edit";
    }

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

    @PostMapping("/events/{id}/remove")
    public String eventsDelete(@PathVariable Long id, Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        Events event = eventsRepository.findById(id).orElseThrow(); // .orElseThrow() исключение если запись не найдена
        checkUserAccess(event, currentUser);
        eventsRepository.delete(event);
        return "redirect:/events";
    }

    @GetMapping("/events/search")
    public String eventsFind(@RequestParam String query, Model model) {
        ArrayList<Events> result = (ArrayList<Events>) eventsRepository.findByTitleContainingIgnoreCase(query);
        model.addAttribute("error", result.isEmpty());
        model.addAttribute("result", result);
        //model.addAttribute("result", result);
        return "events-search";
    }

    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("title", "Связь с нами");
        return "contacts";
    }

    private void checkUserAccess(Events event, User currentUser) {
        if (!currentUser.equals(event.getOwner()) && !currentUser.hasRole(Role.ROLE_ADMIN.name())) {
            throw new AccessDeniedException("Access denied");
        }
    }

}
