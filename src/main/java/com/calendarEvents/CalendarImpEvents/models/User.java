package com.calendarEvents.CalendarImpEvents.models;

import com.calendarEvents.CalendarImpEvents.models.Enams.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an application user
 */
@Entity
@Table(name = "Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The username, must not be empty
     */
    @NotBlank(message = "Username required")
    @Column(name = "username", unique = true)
    private String username;

    /**
     * The password, must not be empty
     */
    @NotBlank(message = "Password must not be empty")
    @Column(name = "password", length = 1000)
    private String password;

    /**
     * Whether the account is active or banned
     */
    @Column(name = "active")
    private boolean active;

    /**
     * The roles of the user
     */
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "User_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    /**
     * The list of events created by this user
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Events> events = new ArrayList<>();

    /**
     * The date and time when this user was created
     */
    private LocalDateTime dateOfCreated;

    /**
     * Sets the date and time the user was created
     */
    @PrePersist
    private void init() {
        dateOfCreated = LocalDateTime.now();
    }

    /**
     * Sets the active state of the account
     * @param active The active state
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the list of events created by this user
     * @return The list of events
     */
    public List<Events> getEvents() {
        return events;
    }

    /**
     * Checks if the user has a given role
     * @param role The role name
     * @return True if the user has that role, false otherwise
     */
    public boolean hasRole(String role) {
        return this.roles.contains(Role.valueOf(role));
    }

    //security

    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
