package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Booking;
import service.BookingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/bookings")
public class BookingController extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            Booking bookingRequest = objectMapper.readValue(request.getInputStream(), Booking.class);

            boolean isSuccess = bookingService.makeBooking(bookingRequest);

            if (isSuccess) {
                response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Court booked successfully!");
                jsonResponse.put("bookingId", bookingRequest.getId());
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
                jsonResponse.put("success", false);
                jsonResponse.put("message", "The selected court is already booked for this time slot.");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid request format: " + e.getMessage());
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}