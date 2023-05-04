package com.calendarEvents.CalendarImpEvents.Controllers;

import com.calendarEvents.CalendarImpEvents.models.Events;
import com.calendarEvents.CalendarImpEvents.repositories.EventsRepositiry;
import jdk.jfr.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class NavbarController {

    @Autowired
    private EventsRepositiry eventsRepository;

    @GetMapping("/events")
    public String events(Model model) {
        Iterable<Events> events = eventsRepository.findAll();
        model.addAttribute("events", events);
        return "events";
    }

    @GetMapping("/events/{date}")
    public String getEventsByDate(@PathVariable String date, Model model) {
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-DD");
//        LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        List<Events> event = eventsRepository.findByDate(localDate);
        model.addAttribute("localDate", localDate);
        model.addAttribute("event", event);
        return "events-data-add";
    }

    @PostMapping("/events/{date}")
    public String eventsAdd(@PathVariable String date, @RequestParam String title,@RequestParam String description, Model model) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        Events event = new Events(title, localDate, description);
        eventsRepository.save(event);
//        boolean matches = date.matches("\\d{4}-\\d{2}-\\d{2}");
//        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
//        Events event = new Events(title, localDate, description);
//        eventsRepository.save(event);
        return "redirect:/events";
    }

    @GetMapping("/event/{id}")
    public String eventsDetails(@PathVariable Long id, Model model) {
        if (!eventsRepository.existsById(id)){
            return "redirect:/events";
        }
        Optional<Events> events = eventsRepository.findById(id);
        ArrayList<Events> result = new ArrayList<>();
        events.ifPresent(result::add);
        model.addAttribute("events", result);
        return "events-details";
    }

    @GetMapping("/events/{id}/edit")
    public String eventsEdit(@PathVariable Long id, Model model) {
        if (!eventsRepository.existsById(id)){
            return "redirect:/events";
        }
        Optional<Events> events = eventsRepository.findById(id);
        ArrayList<Events> result = new ArrayList<>();
        events.ifPresent(result::add);
        model.addAttribute("events", result);
        return "events-edit";
    }

    @PostMapping("/events/{id}/edit")
    public String eventsUpdate(@PathVariable Long id, @RequestParam String title,@RequestParam String description, Model model) {
        Events event = eventsRepository.findById(id).orElseThrow(); // .orElseThrow() исключение если запись не найдена
        event.setTitle(title);
        event.setDescription(description);
        eventsRepository.save(event);
        return "redirect:/events";
    }

    @PostMapping("/events/{id}/remove")
    public String eventsDelete(@PathVariable Long id, Model model) {
        Events event = eventsRepository.findById(id).orElseThrow(); // .orElseThrow() исключение если запись не найдена
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

}
