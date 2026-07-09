package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.TrainerVenue;
import service.TrainerVenueService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/trainer-venues/*")
public class TrainerVenueController extends HttpServlet {

    private final TrainerVenueService venueService = new TrainerVenueService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String trainerIdParam = request.getParameter("trainerId");
        if (trainerIdParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "success", false,
                    "message", "trainerId query parameter is required."
            ));
            return;
        }

        try {
            int trainerId = Integer.parseInt(trainerIdParam);
            List<TrainerVenue> venues = venueService.getVenuesForTrainer(trainerId);
            objectMapper.writeValue(response.getWriter(), venues);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "success", false,
                    "message", "Invalid trainerId."
            ));
        }
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

        TrainerVenue venue = objectMapper.readValue(request.getReader(), TrainerVenue.class);
        String error = venueService.createVenue(venue, userId);

        if (error == null) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Venue created successfully.");
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

        TrainerVenue venue = objectMapper.readValue(request.getReader(), TrainerVenue.class);
        String error = venueService.updateVenue(venue, userId);

        if (error == null) {
            response.setStatus(HttpServletResponse.SC_OK);
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Venue updated successfully.");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", error);
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
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
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Venue id is required.");
            objectMapper.writeValue(response.getWriter(), jsonResponse);
            return;
        }

        try {
            int venueId = Integer.parseInt(pathInfo.substring(1));
            String error = venueService.deleteVenue(venueId, userId);

            if (error == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Venue deleted successfully.");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", error);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid venue id.");
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}
