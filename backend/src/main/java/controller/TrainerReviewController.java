package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.TrainerReview;
import service.TrainerReviewService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/trainer-reviews")
public class TrainerReviewController extends HttpServlet {

    private final TrainerReviewService reviewService = new TrainerReviewService();
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
            objectMapper.writeValue(response.getWriter(), reviewService.getReviews(trainerId));
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
        if (userId == null || userId <= 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "success", false,
                    "message", "Authentication required."
            ));
            return;
        }

        TrainerReview review = objectMapper.readValue(request.getReader(), TrainerReview.class);
        String error = reviewService.createReview(review, userId);

        Map<String, Object> json = new HashMap<>();
        if (error == null) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            json.put("success", true);
            json.put("message", "Review submitted.");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.put("success", false);
            json.put("message", error);
        }

        objectMapper.writeValue(response.getWriter(), json);
    }
}

