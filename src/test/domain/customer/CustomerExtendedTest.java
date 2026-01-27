package domain.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CustomerExtendedTest {

    @Test
    @DisplayName("Customer properties")
    void testCustomer() {
        Customer c = new Customer(1L, "johnd", "John Doe", "john@e.c", "123", "ACTIVE");
        assertEquals(1L, c.id());
        assertEquals("johnd", c.username());
        assertEquals("John Doe", c.fullName());
        assertEquals("john@e.c", c.email());
        assertEquals("123", c.phone());
        assertEquals("ACTIVE", c.status());
    }

    // Individual tests
    @Test void cust01() { assertNotNull(new Customer(1L, "u", "f", "e", "p", "s")); }
    @Test void cust02() { assertEquals(1L, new Customer(1L, "u", "f", "e", "p", "s").id()); }
    @Test void cust03() { assertEquals("u", new Customer(1L, "u", "f", "e", "p", "s").username()); }
    @Test void cust04() { assertEquals("f", new Customer(1L, "u", "f", "e", "p", "s").fullName()); }
    @Test void cust05() { assertEquals("e", new Customer(1L, "u", "f", "e", "p", "s").email()); }
    @Test void cust06() { assertEquals("p", new Customer(1L, "u", "f", "e", "p", "s").phone()); }
    @Test void cust07() { assertEquals("s", new Customer(1L, "u", "f", "e", "p", "s").status()); }
    @Test void cust08() { Customer c1 = new Customer(1L, "u", "f", "e", "p", "s"); Customer c2 = new Customer(1L, "u", "f", "e", "p", "s"); assertEquals(c1, c2); }
    @Test void cust09() { assertNotNull(new Customer(1L, "u", "f", "e", "p", "s").toString()); }
    @Test void cust10() { assertEquals(1L, new Customer(1, "u", "f", "e", "p", "s").id()); }
    @Test void cust11() { assertNotNull(new Customer(0, "", "", "", "", "")); }
    @Test void cust12() { assertNotNull(new Customer(-1, null, null, null, null, null)); }
    @Test void cust13() { Customer c = new Customer(1L, "u", "f", "e", "p", "s"); assertEquals(c.hashCode(), c.hashCode()); }
    @Test void cust14() { Customer c = new Customer(1L, "u", "f", "e", "p", "s"); assertNotEquals(c, null); }
    @Test void cust15() { Customer c = new Customer(1L, "u", "f", "e", "p", "s"); assertEquals(c, c); }
    @Test void cust16() { assertNotNull(new Customer(2L, "u2", "f2", "e2", "p2", "s2")); }
    @Test void cust17() { assertNotNull(new Customer(3L, "u3", "f3", "e3", "p3", "s3")); }
    @Test void cust18() { assertNotNull(new Customer(4L, "u4", "f4", "e4", "p4", "s4")); }
    @Test void cust19() { assertNotNull(new Customer(5L, "u5", "f5", "e5", "p5", "s5")); }
    @Test void cust20() { assertNotNull(new Customer(6L, "u6", "f6", "e6", "p6", "s6")); }
}
