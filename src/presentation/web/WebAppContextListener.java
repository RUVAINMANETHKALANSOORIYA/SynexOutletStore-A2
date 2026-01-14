package presentation.web;

import infrastructure.concurrency.CheckoutQueue;
import infrastructure.jdbc.JdbcUserRepository;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ports.in.AuthService;
import java.util.logging.Logger;

@WebListener
public class WebAppContextListener implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(WebAppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Web application initializing...");

        // Initialize existing services
        var userRepo = new JdbcUserRepository();
        var authService = new AuthService(userRepo);
        sce.getServletContext().setAttribute("authService", authService);

        // Initialize the checkout queue
        CheckoutQueue.getInstance();

        logger.info("Web application initialized successfully");
        System.out.println("Web application initialized - CheckoutQueue ready");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Web application shutting down...");

        // Shutdown the checkout queue
        CheckoutQueue.getInstance().shutdown();

        logger.info("Web application destroyed successfully");
        System.out.println("Web application destroyed - CheckoutQueue shutdown");
    }
}
