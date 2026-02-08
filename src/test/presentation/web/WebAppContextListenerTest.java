package presentation.web;

import fakes.ServletFakes;
import jakarta.servlet.ServletContextEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class WebAppContextListenerTest {

    @BeforeAll
    static void init() throws Exception {
        Class.forName("fakes.JdbcFakes$FakeDriver");
    }

    @Test
    void testLifecycle() {
        WebAppContextListener listener = new WebAppContextListener();
        java.util.Map<String, Object> attributes = new java.util.HashMap<>();
        jakarta.servlet.ServletContext context = (jakarta.servlet.ServletContext) java.lang.reflect.Proxy.newProxyInstance(
            jakarta.servlet.ServletContext.class.getClassLoader(),
            new Class[]{jakarta.servlet.ServletContext.class},
            (proxy, method, args) -> {
                if (method.getName().equals("getAttribute")) return attributes.get(args[0]);
                if (method.getName().equals("setAttribute")) {
                    attributes.put((String) args[0], args[1]);
                    return null;
                }
                return null;
            }
        );
        ServletContextEvent event = new ServletContextEvent(context);
        
        assertDoesNotThrow(() -> listener.contextInitialized(event));
        assertDoesNotThrow(() -> listener.contextDestroyed(event));
    }
}
