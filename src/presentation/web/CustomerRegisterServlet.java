package presentation.web;

import infrastructure.jdbc.JdbcCustomerRepository;
import ports.in.CustomerRegisterService;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;

public class CustomerRegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String fullName = req.getParameter("fullName");
        String email    = req.getParameter("email");
        String phone    = req.getParameter("phone");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        var repo = new JdbcCustomerRepository();
        var service = new CustomerRegisterService(repo);

        try {
            service.register(username, password, fullName, email, phone);
            resp.sendRedirect(req.getContextPath() + "/customer-login.html?registered=1");
        } catch (IllegalArgumentException ex) {
            resp.sendRedirect(req.getContextPath() + "/customer-register.html?error=1");
        }
    }
}
