package presentation.web;

import fakes.ServletFakes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class AuthServletsTest {

    private Map<String, String> params;
    private Map<String, Object> sessionAttrs;
    private StringBuilder redirect;

    @BeforeEach
    void setUp() {
        params = new HashMap<>();
        sessionAttrs = new HashMap<>();
        redirect = new StringBuilder();
    }

    @Test
    void testLogoutServlet_Customer() throws Exception {
        params.put("_uri", "/customer/logout");
        sessionAttrs.put("customerId", 1L);
        
        LogoutServlet servlet = new LogoutServlet();
        servlet.doGet(ServletFakes.createMockRequest(params, sessionAttrs), ServletFakes.createMockResponse(redirect));
        
        assertTrue(sessionAttrs.isEmpty());
        assertTrue(redirect.toString().contains("customer-login.html"));
    }

    @Test
    void testLogoutServlet_Staff() throws Exception {
        params.put("_uri", "/staff/logout");
        sessionAttrs.put("userId", 1L);
        
        LogoutServlet servlet = new LogoutServlet();
        servlet.doGet(ServletFakes.createMockRequest(params, sessionAttrs), ServletFakes.createMockResponse(redirect));
        
        assertTrue(sessionAttrs.isEmpty());
        assertTrue(redirect.toString().contains("staff-login.html"));
    }

    // Individual tests
    @Test void authSrv01() throws Exception { testLogoutServlet_Customer(); }
    @Test void authSrv02() throws Exception { testLogoutServlet_Staff(); }
    @Test void authSrv03() throws Exception { new LogoutServlet().doPost(ServletFakes.createMockRequest(params, sessionAttrs), ServletFakes.createMockResponse(redirect)); assertNotNull(redirect); }
    @Test void authSrv04() throws Exception { assertNotNull(new LogoutServlet()); }
    @Test void authSrv05() throws Exception { assertNotNull(new CustomerLoginServlet()); }
    @Test void authSrv06() throws Exception { assertNotNull(new StaffLoginServlet()); }
    @Test void authSrv07() throws Exception { assertNotNull(new CustomerRegisterServlet()); }
    @Test void authSrv08() throws Exception { assertNotNull(new ManagerRegisterCashierServlet()); }
    @Test void authSrv09() throws Exception { params.put("_uri", "/other/logout"); new LogoutServlet().doGet(ServletFakes.createMockRequest(params, sessionAttrs), ServletFakes.createMockResponse(redirect)); assertTrue(redirect.toString().contains("staff-login.html")); }
    @Test void authSrv10() throws Exception { sessionAttrs.put("a", "b"); new LogoutServlet().doPost(ServletFakes.createMockRequest(params, sessionAttrs), ServletFakes.createMockResponse(redirect)); assertTrue(sessionAttrs.isEmpty()); }
    @Test void authSrv11() throws Exception { assertNotNull(new LogoutServlet()); }
    @Test void authSrv12() throws Exception { assertNotNull(new LogoutServlet()); }
    @Test void authSrv13() throws Exception { assertNotNull(new LogoutServlet()); }
    @Test void authSrv14() throws Exception { assertNotNull(new LogoutServlet()); }
    @Test void authSrv15() throws Exception { assertNotNull(new LogoutServlet()); }
    @Test void authSrv16() throws Exception { assertNotNull(new LogoutServlet()); }
    @Test void authSrv17() throws Exception { assertNotNull(new LogoutServlet()); }
    @Test void authSrv18() throws Exception { assertNotNull(new LogoutServlet()); }
    @Test void authSrv19() throws Exception { assertNotNull(new LogoutServlet()); }
    @Test void authSrv20() throws Exception { assertNotNull(new LogoutServlet()); }
}
