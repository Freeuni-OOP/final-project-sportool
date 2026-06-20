package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;
import service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/register")
public class RegisterController extends HttpServlet {

    private final UserService userService = new UserService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            User userFromFrontend = objectMapper.readValue(request.getReader(), User.class);
            String errorMessage = userService.registerUser(userFromFrontend);

            if (errorMessage == null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                jsonResponse.put("success", true);
                jsonResponse.put("message", "User registered successfully!");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", errorMessage);
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid input format: " + e.getMessage());
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}