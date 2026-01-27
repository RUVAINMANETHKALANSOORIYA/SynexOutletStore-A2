package fakes;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletContext;
import java.lang.reflect.Proxy;
import java.util.*;

public class ServletFakes {

    public static HttpServletRequest createMockRequest(Map<String, String> params, Map<String, Object> sessionAttrs) {
        HttpSession session = (HttpSession) Proxy.newProxyInstance(
            HttpSession.class.getClassLoader(),
            new Class[]{HttpSession.class},
            (proxy, method, args) -> {
                if (method.getName().equals("getAttribute")) return sessionAttrs.get(args[0]);
                if (method.getName().equals("setAttribute")) {
                    sessionAttrs.put((String) args[0], args[1]);
                    return null;
                }
                if (method.getName().equals("removeAttribute")) {
                    sessionAttrs.remove(args[0]);
                    return null;
                }
                if (method.getName().equals("invalidate")) {
                    sessionAttrs.clear();
                    return null;
                }
                return null;
            }
        );

        return (HttpServletRequest) Proxy.newProxyInstance(
            HttpServletRequest.class.getClassLoader(),
            new Class[]{HttpServletRequest.class},
            (proxy, method, args) -> {
                if (method.getName().equals("getParameter")) return params.get(args[0]);
                if (method.getName().equals("getSession")) return session;
                if (method.getName().equals("getContextPath")) return "";
                if (method.getName().equals("getRequestURI")) return params.getOrDefault("_uri", "/");
                return null;
            }
        );
    }

    public static HttpServletResponse createMockResponse(StringBuilder redirectOut) {
        java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.StringWriter());
        return (HttpServletResponse) Proxy.newProxyInstance(
            HttpServletResponse.class.getClassLoader(),
            new Class[]{HttpServletResponse.class},
            (proxy, method, args) -> {
                if (method.getName().equals("sendRedirect")) {
                    redirectOut.append(args[0]);
                    return null;
                }
                if (method.getName().equals("getWriter")) {
                    return writer;
                }
                if (method.getName().equals("setContentType")) return null;
                if (method.getName().equals("sendError")) return null;
                return null;
            }
        );
    }
}
