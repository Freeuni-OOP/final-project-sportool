package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.TrainerBooking;
import service.TrainerBookingService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/trainer-bookings/*")
public class TrainerBookingController extends HttpServlet {

    private final TrainerBookingService bookingService = new TrainerBookingService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Integer userId = (Integer) request.getAttribute("authenticatedUserId");
        if (userId == null || userId <= 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "success", false,
                    "message", "Authentication required."
            ));
            return;
        }

        if ("true".equalsIgnoreCase(request.getParameter("forTrainer"))) {
            objectMapper.writeValue(response.getWriter(), bookingService.getTrainerBookings(userId));
            return;
        }

        objectMapper.writeValue(response.getWriter(), bookingService.getPlayerBookings(userId));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Integer userId = (Integer) request.getAttribute("authenticatedUserId");
        Map<String, Object> jsonResponse = new HashMap<>();

        if (userId == null || userId <= 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Authentication required.");
            objectMapper.writeValue(response.getWriter(), jsonResponse);
            return;
        }

        TrainerBooking booking = objectMapper.readValue(request.getInputStream(), TrainerBooking.class);
        String error = bookingService.createBookingRequest(booking, userId);

        if (error == null) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Booking request submitted.");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", error);
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Integer userId = (Integer) request.getAttribute("authenticatedUserId");
        Map<String, Object> jsonResponse = new HashMap<>();

        if (userId == null || userId <= 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Authentication required.");
            objectMapper.writeValue(response.getWriter(), jsonResponse);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || !pathInfo.endsWith("/status")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Booking id and /status are required.");
            objectMapper.writeValue(response.getWriter(), jsonResponse);
            return;
        }

        String idPart = pathInfo.substring(1, pathInfo.length() - "/status".length());
        try {
            int bookingId = Integer.parseInt(idPart);
            @SuppressWarnings("unchecked")
            Map<String, String> body = objectMapper.readValue(request.getReader(), Map.class);
            String status = body.get("status");

            String error = bookingService.updateBookingStatus(bookingId, userId, status);
            if (error == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Booking status updated.");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", error);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid booking id.");
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}
