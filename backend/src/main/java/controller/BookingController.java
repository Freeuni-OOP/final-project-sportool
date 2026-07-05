package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Booking;
import service.BookingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/bookings")
public class BookingController extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String courtIdParam = request.getParameter("courtId");
        String dateParam = request.getParameter("date");
        String mineParam = request.getParameter("mine");

        if ("true".equalsIgnoreCase(mineParam)) {
            Integer authenticatedUserId = (Integer) request.getAttribute("authenticatedUserId");
            if (authenticatedUserId == null || authenticatedUserId <= 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(response.getWriter(), Map.of(
                        "success", false,
                        "message", "Authentication required."
                ));
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    response.getWriter(),
                    bookingService.getUserBookings(authenticatedUserId)
            );
            return;
        }

        if (courtIdParam == null || dateParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "success", false,
                    "message", "courtId and date query parameters are required."
            ));
            return;
        }

        try {
            int courtId = Integer.parseInt(courtIdParam);
            LocalDate date = LocalDate.parse(dateParam);
            List<Booking> bookings = bookingService.getCourtBookingsForDate(courtId, date);

            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), bookings);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "success", false,
                    "message", "Invalid query parameters: " + e.getMessage()
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            Booking bookingRequest = objectMapper.readValue(request.getInputStream(), Booking.class);

            Integer authenticatedUserId = (Integer) request.getAttribute("authenticatedUserId");
            if (authenticatedUserId != null && authenticatedUserId > 0) {
                bookingRequest.setUserId(authenticatedUserId);
            }

            String errorMessage = bookingService.makeBooking(bookingRequest);

            if (errorMessage == null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Court booked successfully!");
                jsonResponse.put("bookingId", bookingRequest.getId());
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                jsonResponse.put("success", false);
                jsonResponse.put("message", errorMessage);
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid request format: " + e.getMessage());
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}