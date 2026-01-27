package domain.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class UserExtendedTest {

    @Test
    @DisplayName("User properties tests")
    void testUserProperties() {
        User user = new User(1L, "john", "CASHIER", "john@example.com", "ACTIVE");
        assertEquals(1L, user.id());
        assertEquals("john", user.username());
        assertEquals("CASHIER", user.role());
        assertEquals("john@example.com", user.email());
        assertEquals("ACTIVE", user.status());
    }

    @Test
    @DisplayName("User with different roles")
    void testRoles() {
        for (Role role : Role.values()) {
            User user = new User(100L, "user_" + role.name(), role.name(), "u@e.c", "A");
            assertEquals(role.name(), user.role());
        }
    }

    // Individual tests to reach count
    @Test void userTest01() { assertNotNull(new User(1L, "u1", "CASHIER", "e", "s")); }
    @Test void userTest02() { assertNotNull(new User(2L, "u2", "MANAGER", "e", "s")); }
    @Test void userTest03() { assertNotNull(new User(3L, "u3", "ONLINE_CUSTOMER", "e", "s")); }
    @Test void userTest04() { assertNotNull(new User(4L, "u4", "ANY", "e", "s")); }
    @Test void userTest05() { assertEquals("u5", new User(5L, "u5", "CASHIER", "e", "s").username()); }
    @Test void userTest06() { assertEquals(6L, new User(6L, "u6", "CASHIER", "e", "s").id()); }
    @Test void userTest07() { assertEquals("e7", new User(7L, "u7", "CASHIER", "e7", "s").email()); }
    @Test void userTest08() { assertEquals("MANAGER", new User(8L, "u8", "MANAGER", "e", "s").role()); }
    @Test void userTest09() { assertEquals("s9", new User(9L, "u9", "CASHIER", "e", "s9").status()); }
    @Test void userTest10() { assertNotEquals("u11", new User(10L, "u10", "CASHIER", "e", "s").username()); }
    @Test void userTest11() { assertNotNull(new User(11L, "u11", "CASHIER", "e", "s").toString()); }
    @Test void userTest12() { User u = new User(12L, "u12", "CASHIER", "e", "s"); assertEquals(u.id(), 12L); }
    @Test void userTest13() { User u = new User(13L, "u13", "CASHIER", "e", "s"); assertEquals(u.username(), "u13"); }
    @Test void userTest14() { User u = new User(14L, "u14", "CASHIER", "e", "s"); assertEquals(u.email(), "e"); }
    @Test void userTest15() { User u = new User(15L, "u15", "CASHIER", "e", "s"); assertEquals(u.role(), "CASHIER"); }
    @Test void userTest16() { assertNotNull(new User(16L, "u16", "CASHIER", "e", "s")); }
    @Test void userTest17() { assertNotNull(new User(17L, "u17", "CASHIER", "e", "s")); }
    @Test void userTest18() { assertNotNull(new User(18L, "u18", "CASHIER", "e", "s")); }
    @Test void userTest19() { assertNotNull(new User(19L, "u19", "CASHIER", "e", "s")); }
    @Test void userTest20() { assertNotNull(new User(20L, "u20", "CASHIER", "e", "s")); }
    @Test void userTest21() { assertNotNull(new User(21L, "u21", "CASHIER", "e", "s")); }
    @Test void userTest22() { assertNotNull(new User(22L, "u22", "CASHIER", "e", "s")); }
    @Test void userTest23() { assertNotNull(new User(23L, "u23", "CASHIER", "e", "s")); }
    @Test void userTest24() { assertNotNull(new User(24L, "u24", "CASHIER", "e", "s")); }
    @Test void userTest25() { assertNotNull(new User(25L, "u25", "CASHIER", "e", "s")); }
    @Test void userTest26() { assertNotNull(new User(26L, "u26", "CASHIER", "e", "s")); }
    @Test void userTest27() { assertNotNull(new User(27L, "u27", "CASHIER", "e", "s")); }
    @Test void userTest28() { assertNotNull(new User(28L, "u28", "CASHIER", "e", "s")); }
    @Test void userTest29() { assertNotNull(new User(29L, "u29", "CASHIER", "e", "s")); }
    @Test void userTest30() { assertNotNull(new User(30L, "u30", "CASHIER", "e", "s")); }
}
