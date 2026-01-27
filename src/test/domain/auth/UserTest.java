package domain.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Should create user and retrieve properties correctly")
    void testUserProperties() {
        User user = new User(1L, "john_doe", "MANAGER", "john@example.com", "ACTIVE");

        assertEquals(1L, user.id());
        assertEquals("john_doe", user.username());
        assertEquals("MANAGER", user.role());
        assertEquals("john@example.com", user.email());
        assertEquals("ACTIVE", user.status());
    }
}
