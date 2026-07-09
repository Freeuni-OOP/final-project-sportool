package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Trainer;
import model.TrainerDetail;
import service.TrainerService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/trainers/*")
public class TrainerController extends HttpServlet {

    private final TrainerService trainerService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TrainerController() {
        this.trainerService = new TrainerService();
    }

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        if ("/me".equals(pathInfo)) {
            handleGetMe(request, response);
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            List<TrainerDetail> trainers = trainerService.getAllTrainerDetails();
            objectMapper.writeValue(response.getWriter(), trainers);
        } else {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                TrainerDetail trainer = trainerService.getTrainerDetailById(id);

                Map<String, Object> jsonResponse = new HashMap<>();
                if (trainer == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Trainer not found.");
                    objectMapper.writeValue(response.getWriter(), jsonResponse);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    objectMapper.writeValue(response.getWriter(), trainer);
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getWriter(), Map.of(
                        "success", false,
                        "message", "Invalid trainer id."
                ));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Trainer trainer = objectMapper.readValue(request.getReader(), Trainer.class);
        boolean success = trainerService.addTrainer(trainer);

        Map<String, Object> jsonResponse = new HashMap<>();
        if (success) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Trainer added successfully.");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Trainer could not be added.");
        }
        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }

    private void handleGetMe(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer userId = (Integer) request.getAttribute("authenticatedUserId");
        String role = (String) request.getAttribute("authenticatedRole");
        String fullName = (String) request.getAttribute("authenticatedFullName");

        Map<String, Object> jsonResponse = new HashMap<>();
        if (userId == null || userId <= 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Authentication required.");
            objectMapper.writeValue(response.getWriter(), jsonResponse);
            return;
        }

        if (!"COACH".equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Coach access required.");
            objectMapper.writeValue(response.getWriter(), jsonResponse);
            return;
        }

        Trainer trainer = trainerService.ensureTrainerProfile(userId, fullName);
        if (trainer == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Trainer profile could not be loaded.");
            objectMapper.writeValue(response.getWriter(), jsonResponse);
            return;
        }

        TrainerDetail detail = trainerService.getTrainerDetailById(trainer.getId());
        response.setStatus(HttpServletResponse.SC_OK);
        jsonResponse.put("success", true);
        jsonResponse.put("trainer", detail);
        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}