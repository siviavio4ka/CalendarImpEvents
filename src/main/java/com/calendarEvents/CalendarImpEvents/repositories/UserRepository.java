package com.calendarEvents.CalendarImpEvents.repositories;

import com.calendarEvents.CalendarImpEvents.models.Events;
import com.calendarEvents.CalendarImpEvents.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository to perform CRUD operations on users
 * Extends the JpaRepository to provide additional methods to query the user
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a User by their username
     * @param username the username of the User to find
     * @return an Optional containing the User, or an empty Optional if no User found
     */
    Optional<User> findByUsername(String username);
}
