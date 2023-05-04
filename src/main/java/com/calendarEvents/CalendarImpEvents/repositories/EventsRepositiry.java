package com.calendarEvents.CalendarImpEvents.repositories;

import com.calendarEvents.CalendarImpEvents.models.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventsRepositiry extends JpaRepository<Events, Long> {
    List<Events> findByDate(LocalDate date);

    List<Events> findByTitleContainingIgnoreCase(String title);
}
