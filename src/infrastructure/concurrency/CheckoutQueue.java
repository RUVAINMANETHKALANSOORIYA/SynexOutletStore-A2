package infrastructure.concurrency;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Singleton thread pool executor for handling checkout requests with bounded queuing.
 * Provides server-side concurrency with explicit queue management and rejection handling.
 */
public class CheckoutQueue {
    private static final Logger logger = Logger.getLogger(CheckoutQueue.class.getName());

    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 4;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final int QUEUE_CAPACITY = 100;

    private static volatile CheckoutQueue instance;
    private final ThreadPoolExecutor executor;

    private CheckoutQueue() {
        this.executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
            new ThreadFactory() {
                private int counter = 0;
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "checkout-worker-" + (++counter));
                    t.setDaemon(false);
                    return t;
                }
            },
            new ThreadPoolExecutor.AbortPolicy()
        );

        logger.info("CheckoutQueue initialized with pool size: " + CORE_POOL_SIZE +
                   ", queue capacity: " + QUEUE_CAPACITY);
    }

    public static CheckoutQueue getInstance() {
        if (instance == null) {
            synchronized (CheckoutQueue.class) {
                if (instance == null) {
                    instance = new CheckoutQueue();
                }
            }
        }
        return instance;
    }

    /**
     * Submit a checkout task to the thread pool
     * @param task The checkout task to execute
     * @return Future containing the order ID
     * @throws RejectedExecutionException if queue is full or executor is shutdown
     */
    public Future<Long> submit(Callable<Long> task) throws RejectedExecutionException {
        logger.info("Submitting task to checkout queue. Queue size: " + getQueueSize() +
                   ", Active threads: " + getActiveCount());
        return executor.submit(task);
    }

    /**
     * Get current queue size (pending tasks)
     */
    public int getQueueSize() {
        return executor.getQueue().size();
    }

    /**
     * Get number of actively executing threads
     */
    public int getActiveCount() {
        return executor.getActiveCount();
    }

    /**
     * Get pool size
     */
    public int getPoolSize() {
        return executor.getPoolSize();
    }

    /**
     * Shutdown the executor gracefully
     */
    public void shutdown() {
        logger.info("Shutting down CheckoutQueue...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                logger.warning("Executor did not terminate gracefully, forcing shutdown");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.warning("Interrupted while waiting for executor termination");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("CheckoutQueue shutdown completed");
    }

    /**
     * Check if executor is shutdown
     */
    public boolean isShutdown() {
        return executor.isShutdown();
    }
}
