package com.calendarEvents.CalendarImpEvents.models.Enams;

import org.springframework.security.core.GrantedAuthority;

/**
 * Enum representing the roles of a user
 */
public enum Role implements GrantedAuthority {
    /**
     * Role for a regular user
     */
    ROLE_USER,
    /**
     * Role for an administrator
     */
    ROLE_ADMIN;

    /**
     * Returns the name of the role
     * @return role name
     */
    @Override
    public String getAuthority() {
        return name();
    }
}
