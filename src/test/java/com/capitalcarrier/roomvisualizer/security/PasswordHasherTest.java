package com.capitalcarrier.roomvisualizer.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void hashPassword_returnsNonNullHash() {
        String password = "securePassword123";
        String hash = PasswordHasher.hashPassword(password);
        
        assertNotNull(hash);
        assertNotEquals(password, hash);
    }

    @Test
    void hashPassword_samePassword_sameHash() {
        String password = "securePassword123";
        String hash1 = PasswordHasher.hashPassword(password);
        String hash2 = PasswordHasher.hashPassword(password);
        
        assertEquals(hash1, hash2);
    }

    @Test
    void hashPassword_nullPassword_returnsNull() {
        assertNull(PasswordHasher.hashPassword(null));
    }

    @Test
    void verifyPassword_correctPassword_returnsTrue() {
        String password = "securePassword123";
        String hash = PasswordHasher.hashPassword(password);
        
        assertTrue(PasswordHasher.verifyPassword(password, hash));
    }

    @Test
    void verifyPassword_incorrectPassword_returnsFalse() {
        String password = "securePassword123";
        String wrongPassword = "wrongPassword123";
        String hash = PasswordHasher.hashPassword(password);
        
        assertFalse(PasswordHasher.verifyPassword(wrongPassword, hash));
    }

    @Test
    void verifyPassword_nullInputs_returnsFalse() {
        String hash = PasswordHasher.hashPassword("test");
        
        assertFalse(PasswordHasher.verifyPassword(null, hash));
        assertFalse(PasswordHasher.verifyPassword("test", null));
        assertFalse(PasswordHasher.verifyPassword(null, null));
    }
}
