package presentation.web;

import domain.customer.Customer;
import infrastructure.jdbc.JdbcCustomerRepository;
import ports.in.CustomerAuthService;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;

public class CustomerLoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        var repo = new JdbcCustomerRepository();
        var auth = new CustomerAuthService(repo);

        Optional<Customer> c = auth.login(username, password);
        if (c.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/customer-login.html?error=1");
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("customerId", c.get().id());
        session.setAttribute("customerUsername", c.get().username());

        // After customer login, go to online store home
        resp.sendRedirect(req.getContextPath() + "/store/home");
    }
}
