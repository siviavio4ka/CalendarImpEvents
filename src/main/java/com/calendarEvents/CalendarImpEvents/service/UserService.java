package com.calendarEvents.CalendarImpEvents.service;

import com.calendarEvents.CalendarImpEvents.models.User;
import com.calendarEvents.CalendarImpEvents.repositories.EventsRepositiry;
import com.calendarEvents.CalendarImpEvents.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service class for performing user-related operations
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    /**
     * Repository for performing operations related to users
     */
    private final UserRepository userRepository;
    /**
     * Repository for performing operations related to events
     */
    private final EventsRepositiry eventsRepositiry;

    /**
     * Method that loads a user by username
     * @param username username
     * @return loaded UserDetails
     * @throws UsernameNotFoundException if user not found
     * @throws DisabledException if user is banned
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (!user.isActive()) {
            throw new DisabledException("User is banned");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    /**
     * Method to get all users
     * @return list of all users
     */
    public List<User> list() {
        return userRepository.findAll();
    }

    /**
     * A method that bans or activates a user by id
     * @param id user ID
     */
    public void banUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            if (user.isActive()) {
                user.setActive(false);
            } else {
                user.setActive(true);
            }
        }
        userRepository.save(user);
    }

    /**
     * Method that deletes a user by id and related events
     * @param id user ID
     */
    @Transactional
    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            eventsRepositiry.deleteAllByOwner(user);
            userRepository.deleteById(id);
        }else {
            log.info("User with id {} not found", id);
        }
    }

    /**
     * Method that gets the user by id
     * @param id user ID
     * @return User
     * @throws EntityNotFoundException if user not found
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }
}
