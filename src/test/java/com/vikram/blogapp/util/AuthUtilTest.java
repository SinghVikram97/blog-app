package com.vikram.blogapp.util;

import com.vikram.blogapp.entities.Role;
import org.junit.jupiter.api.Test;

import static com.vikram.blogapp.util.AuthUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthUtilTest {

    private final String EMAIL_IN_JWT="jwtemail@gmail.com";
    private final String EMAIL_IN_REQUEST="requestemail@gmail.com";
    private final String ADMIN_ROLE= Role.ROLE_ADMIN.name();
    private final String USER_ROLE= Role.ROLE_USER.name();


    @Test
    void testIsSameUserOrAdmin_whenAdmin_returnTrue() {
        assertTrue(isSameUserOrAdmin(EMAIL_IN_JWT, ADMIN_ROLE, EMAIL_IN_REQUEST));
    }

    @Test
    void testIsSameUserOrAdmin_whenSameUser_returnTrue() {
        assertTrue(isSameUserOrAdmin(EMAIL_IN_JWT, USER_ROLE, EMAIL_IN_JWT));
    }

    @Test
    void testIsSameUserOrAdmin_whenDifferentUserAndNotAdmin_returnsFalse() {
        assertFalse(isSameUserOrAdmin(EMAIL_IN_JWT,USER_ROLE,EMAIL_IN_REQUEST));
    }

    @Test
    void testIsAdmin_whenAdminRole_returnsTrue() {
        assertTrue(isAdmin(ADMIN_ROLE));
    }

    @Test
    void testIsAdmin_whenUserRole_returnsFalse() {
        assertFalse(isAdmin(USER_ROLE));
    }

    @Test
    void testIsSameUser_whenSameUser_returnsTrue() {
        assertTrue(isSameUser(EMAIL_IN_JWT, EMAIL_IN_JWT));
    }

    @Test
    void testIsSameUser_whenDifferentUser_returnsFalse() {
        assertFalse(isSameUser(EMAIL_IN_JWT, EMAIL_IN_REQUEST));
    }
}