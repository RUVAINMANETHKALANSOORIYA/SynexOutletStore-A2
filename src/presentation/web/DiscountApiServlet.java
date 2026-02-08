package presentation.web;

import application.DiscountServiceImpl;
import infrastructure.jdbc.JdbcDiscountRepository;
import ports.in.DiscountService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DiscountApiServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            DiscountService discountService = new DiscountServiceImpl(new JdbcDiscountRepository());

            if ("/apply".equals(pathInfo)) {
                handleApplyDiscount(request, response, discountService);
            } else if ("/best-auto".equals(pathInfo)) {
                handleBestAutoDiscount(request, response, discountService);
            } else {
                sendError(response, 404, "Endpoint not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, 500, "Internal server error: " + e.getMessage());
        }
    }

    private void handleApplyDiscount(HttpServletRequest request, HttpServletResponse response,
                                   DiscountService discountService) throws IOException {

        // Parse request JSON manually
        Map<String, String> requestData = parseSimpleJson(request);
        String discountCode = requestData.get("discountCode");
        BigDecimal subtotal = new BigDecimal(requestData.get("subtotal"));

        // Apply discount
        DiscountService.DiscountResult result = discountService.applyBestDiscount(subtotal, discountCode);

        // Create JSON response manually
        String jsonResponse = createDiscountResultJson(result, subtotal);
        response.getWriter().write(jsonResponse);
    }

    private void handleBestAutoDiscount(HttpServletRequest request, HttpServletResponse response,
                                      DiscountService discountService) throws IOException {

        // Parse request JSON manually
        Map<String, String> requestData = parseSimpleJson(request);
        BigDecimal subtotal = new BigDecimal(requestData.get("subtotal"));

        // Apply best auto discount using FIFO logic
        DiscountService.DiscountResult result = discountService.applyBestDiscountAuto(subtotal);

        // Create JSON response manually
        String jsonResponse = createDiscountResultJson(result, subtotal);
        response.getWriter().write(jsonResponse);
    }

    private Map<String, String> parseSimpleJson(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        String json = sb.toString();
        Map<String, String> data = new HashMap<>();

        // Simple JSON parsing for our specific format
        // {"discountCode": "TEST20", "subtotal": 150}
        json = json.replace("{", "").replace("}", "").replace("\"", "");
        String[] pairs = json.split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                data.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }

        return data;
    }

    private String createDiscountResultJson(DiscountService.DiscountResult result, BigDecimal originalSubtotal) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\": ").append(result.appliedDiscount() != null ? "true" : "false").append(",");
        json.append("\"message\": \"").append(escapeJson(result.message())).append("\",");
        json.append("\"discountAmount\": ").append(result.discountAmount().doubleValue()).append(",");
        json.append("\"finalAmount\": ").append(result.finalAmount().doubleValue());

        if (result.appliedDiscount() != null) {
            json.append(",\"discount\": {");
            json.append("\"discountId\": ").append(result.appliedDiscount().discountId()).append(",");
            json.append("\"discountCode\": \"").append(escapeJson(result.appliedDiscount().discountCode())).append("\",");
            json.append("\"description\": \"").append(escapeJson(result.appliedDiscount().description())).append("\",");
            json.append("\"discountType\": \"").append(result.appliedDiscount().discountType().name()).append("\",");
            json.append("\"discountValue\": ").append(result.appliedDiscount().discountValue().doubleValue());
            json.append("}");
        }

        json.append("}");
        return json.toString();
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        String errorJson = String.format("{\"success\": false, \"message\": \"%s\"}", escapeJson(message));
        response.getWriter().write(errorJson);
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
