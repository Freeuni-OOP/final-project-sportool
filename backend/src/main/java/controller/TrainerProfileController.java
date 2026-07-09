package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.TrainerProfileService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/trainer-profile/me")
public class TrainerProfileController extends HttpServlet {

    private final TrainerProfileService profileService = new TrainerProfileService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
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

        @SuppressWarnings("unchecked")
        Map<String, Object> body = objectMapper.readValue(request.getReader(), Map.class);
        String description = body.get("description") != null ? String.valueOf(body.get("description")) : null;

        String error = profileService.upsertMyDescription(userId, description);

        Map<String, Object> json = new HashMap<>();
        if (error == null) {
            response.setStatus(HttpServletResponse.SC_OK);
            json.put("success", true);
            json.put("message", "Description updated.");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.put("success", false);
            json.put("message", error);
        }

        objectMapper.writeValue(response.getWriter(), json);
    }
}

