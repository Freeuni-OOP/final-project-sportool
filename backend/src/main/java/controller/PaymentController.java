package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PaymentRequest;
import service.PaymentService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/payments/process")
public class PaymentController extends HttpServlet {

    private final PaymentService paymentService = new PaymentService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            PaymentRequest paymentRequest = objectMapper.readValue(request.getInputStream(), PaymentRequest.class);
            String errorMessage = paymentService.processPayment(paymentRequest);

            if (errorMessage != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", errorMessage);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Payment successful.");
                jsonResponse.put("paymentReference", paymentService.generatePaymentReference());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid payment request.");
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}
