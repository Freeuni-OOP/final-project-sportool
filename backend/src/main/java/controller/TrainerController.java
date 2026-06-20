package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Trainer;
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

        if (pathInfo == null || pathInfo.equals("/")) {
            List<Trainer> trainers = trainerService.getAllTrainers();
            objectMapper.writeValue(response.getWriter(), trainers);
        } else {
            int id = Integer.parseInt(pathInfo.substring(1));
            Trainer trainer = trainerService.getTrainerById(id);

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
}