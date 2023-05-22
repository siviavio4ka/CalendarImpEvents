package com.calendarEvents.CalendarImpEvents.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Class representing events
 */
@Entity
@Table(name = "Events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Events {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    /**
     * The title of the event
     */
    @Column(name = "title")
    private String title;

    /**
     * The date of the event
     */
    @Column(name = "date")
    private LocalDate date;

    /**
     * The description of the event
     */
    @Column(name = "description")
    private String description;

    /**
     * The creator of the event
     */
    @ManyToOne
    private User user;

    /**
     * The owner of the event
     */
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    /**
     * Sets the user who created the event
     * @param user The user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Creates an event
     * @param title Event name
     * @param date Event date
     * @param description Description of the event
     */
    public Events(String title, LocalDate date, String description) {
        this.title = title;
        this.date = date;
        this.description = description;
    }
}
