package infrastructure.concurrency;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class CheckoutQueueShutdownTest {

    @Test
    void testShutdown() throws Exception {
        CheckoutQueue queue = CheckoutQueue.getInstance();
        assertFalse(queue.isShutdown());
        
        queue.shutdown();
        assertTrue(queue.isShutdown());
        
        // Reset singleton using reflection so other tests don't fail
        Field instanceField = CheckoutQueue.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        
        CheckoutQueue newQueue = CheckoutQueue.getInstance();
        assertNotSame(queue, newQueue);
        assertFalse(newQueue.isShutdown());
    }
}
