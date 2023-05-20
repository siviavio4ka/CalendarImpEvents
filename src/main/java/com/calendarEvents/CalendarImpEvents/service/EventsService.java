package com.calendarEvents.CalendarImpEvents.service;

import com.calendarEvents.CalendarImpEvents.models.Events;
import com.calendarEvents.CalendarImpEvents.repositories.EventsRepositiry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventsService {
    @Autowired
    private EventsRepositiry eventsRepositiry;

    public List<Events> findByTitleContainingIgnoreCase(String title) {
        return eventsRepositiry.findByTitleContainingIgnoreCase(title);
    }
    public List<Events> findByDate(LocalDate date) {
        return eventsRepositiry.findByDate(date);
    }
}
