package com.calendarEvents.CalendarImpEvents.service;

import com.calendarEvents.CalendarImpEvents.models.User;
import com.calendarEvents.CalendarImpEvents.repositories.EventsRepositiry;
import com.calendarEvents.CalendarImpEvents.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    EventsRepositiry eventsRepositiry;

    @Test
    public void testLoadUserByUsername() {
        String username = "username";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("password");
        mockUser.setActive(true);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        UserDetails result = userService.loadUserByUsername(username);

        Assert.assertEquals(username, result.getUsername());
        Assert.assertEquals("password", result.getPassword());
        Assert.assertEquals(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")), new ArrayList<>(result.getAuthorities()));
    }

    @Test
    void listTest() {
        List<User> users = Arrays.asList(new User(),new User());
        Mockito.when(userRepository.findAll()).thenReturn(users);

        UserService userService1 = new UserService(userRepository, null);
        List<User> userList = userService1.list();

        Assert.assertEquals(users, userList);
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    void banUser_whenActive() {
        Long id = 1L;

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Assert.assertTrue(user.isActive());

            userService.banUser(id);
            Assert.assertFalse(user.isActive());
        }

    }
    @Test
    void banUser_whenBanned() {
        Long id = 1L;
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userService.banUser(id);
            Assert.assertFalse(user.isActive());

            userService.banUser(id);
            Assert.assertTrue(user.isActive());
        }

    }
    @Test
    void banUser_whenNotFound() {
        Long id = 0L;

        userService.banUser(id);

        Assert.assertFalse(userRepository.findById(id).isPresent());
    }

    @Test
    void deleteUser_whenFound() {
        Long id = 1L;

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            userService.deleteUser(id);

            Assert.assertFalse(userRepository.findById(id).isPresent());
        }
    }

    @Test
    void deleteUser_whenNotFound() {
        Long id = 0L;

        userService.deleteUser(id);

        Mockito.verify(userRepository, Mockito.never()).deleteById(id);
        Mockito.verify(eventsRepositiry, Mockito.never()).deleteAllByOwner(any());
    }



    @Test
    void getUserById_found() {
        Long id = 1L;
        User user = new User();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User actualUser = userService.getUserById(id);

        Assert.assertEquals(user, actualUser);
    }

    @Test
    void getUserById_notFound() {
        Long id = 1L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assert.assertThrows(EntityNotFoundException.class, () ->
                userService.getUserById(id));
    }
}