package infrastructure.concurrency;

import org.junit.jupiter.api.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class CheckoutQueueTest {
    private CheckoutQueue queue;

    @BeforeEach
    void setUp() {
        queue = CheckoutQueue.getInstance();
    }

    @Test
    @DisplayName("Should submit and execute tasks")
    void testSubmitTask() throws Exception {
        Future<Long> future = queue.submit(() -> {
            Thread.sleep(100);
            return 123L;
        });
        
        assertEquals(123L, future.get(1, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("Should track queue size and active count")
    void testQueueStatus() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        // Submit a task that waits for a latch
        queue.submit(() -> {
            latch.await();
            return 1L;
        });
        
        // Active count should be at least 1
        assertTrue(queue.getActiveCount() >= 1);
        
        latch.countDown();
    }

    @Test
    @DisplayName("Should handle many concurrent submissions")
    void testConcurrency() throws Exception {
        int taskCount = 20;
        CountDownLatch finishedLatch = new CountDownLatch(taskCount);
        
        for (int i = 0; i < taskCount; i++) {
            queue.submit(() -> {
                finishedLatch.countDown();
                return 1L;
            });
        }
        
        assertTrue(finishedLatch.await(5, TimeUnit.SECONDS));
    }
}
