package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dao.BookingDaoSql;
import model.Booking;

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

    private final BookingDaoSql bookingDao = new BookingDaoSql();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        // Required for Jackson to properly serialize/deserialize LocalDateTime formats
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            // 1. Read JSON input and convert to Booking object
            Booking bookingRequest = objectMapper.readValue(request.getInputStream(), Booking.class);

            // 2. Business Logic Validation: Check for time slots overlap
            boolean isAvailable = bookingDao.isCourtAvailable(
                    bookingRequest.getCourtId(),
                    bookingRequest.getStartTime(),
                    bookingRequest.getEndTime()
            );

            if (!isAvailable) {
                response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
                jsonResponse.put("success", false);
                jsonResponse.put("message", "The selected court is already booked for this time slot.");
                objectMapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }

            // 3. Save booking to the database
            boolean isSaved = bookingDao.createBooking(bookingRequest);

            if (isSaved) {
                response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Court booked successfully!");
                jsonResponse.put("bookingId", bookingRequest.getId());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Database error occurred while processing the booking.");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid request format: " + e.getMessage());
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}