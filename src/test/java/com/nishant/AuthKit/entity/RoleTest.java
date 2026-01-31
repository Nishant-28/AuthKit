package com.nishant.AuthKit.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void shouldHaveAdminRole() {
        assertNotNull(Role.ADMIN);
        assertEquals("ADMIN", Role.ADMIN.name());
    }

    @Test
    void shouldHaveUserRole() {
        assertNotNull(Role.USER);
        assertEquals("USER", Role.USER.name());
    }

    @Test
    void shouldHaveExactlyTwoRoles() {
        Role[] roles = Role.values();
        assertEquals(2, roles.length);
        assertTrue(java.util.Arrays.asList(roles).contains(Role.ADMIN));
        assertTrue(java.util.Arrays.asList(roles).contains(Role.USER));
    }
}
