package domain.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    @DisplayName("Should create Customer and retrieve properties")
    void testCustomer() {
        Customer customer = new Customer(1L, "john_doe", "John Doe", "john@example.com", "+94771234567", "ACTIVE");
        
        assertEquals(1L, customer.id());
        assertEquals("john_doe", customer.username());
        assertEquals("John Doe", customer.fullName());
        assertEquals("john@example.com", customer.email());
        assertEquals("+94771234567", customer.phone());
        assertEquals("ACTIVE", customer.status());
    }
}
