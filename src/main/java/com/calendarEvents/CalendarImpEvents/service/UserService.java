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

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EventsRepositiry eventsRepositiry;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (!user.isActive()) {
            throw new DisabledException("User is banned");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public List<User> list() {
        return userRepository.findAll();
    }

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

    @Transactional
    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.get();
        eventsRepositiry.deleteAllByOwner(user);
        userRepository.deleteById(id);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }
}
