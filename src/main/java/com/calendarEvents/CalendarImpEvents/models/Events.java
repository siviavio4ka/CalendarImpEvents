package com.calendarEvents.CalendarImpEvents.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private User user;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public void setUser(User user) {
        this.user = user;
    }

    public Events(String title, LocalDate date, String description) {
        this.title = title;
        this.date = date;
        this.description = description;
    }
}
