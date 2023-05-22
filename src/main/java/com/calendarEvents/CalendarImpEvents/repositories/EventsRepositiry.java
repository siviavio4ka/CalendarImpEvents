package com.calendarEvents.CalendarImpEvents.repositories;

import com.calendarEvents.CalendarImpEvents.models.Events;
import com.calendarEvents.CalendarImpEvents.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for performing CRUD operations on events
 * Extends the JpaRepository to provide additional methods to query Event entities
 */
@Repository
public interface EventsRepositiry extends JpaRepository<Events, Long> {
    /**
     * Finds all events for a given date
     * @param date the date to find events for
     * @return list of events for the given date
     */
    List<Events> findByDate(LocalDate date);

    /**
     * Finds all events by title, case insensitive
     * @param title Event title
     * @return the list of matching events
     */
    List<Events> findByTitleContainingIgnoreCase(String title);

    /**
     * Finds all events owned by a given user
     * @param user the owner of the events
     * @return list of user events
     */
    List<Events> findEventsByOwner(User user);

    /**
     * Deletes all events owned by a given user
     * @param user the owner of the events to delete
     * @return the list of deleted events
     */
    List<Events> deleteAllByOwner(User user);
}
