package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.LoginService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/login")
public class LoginController extends HttpServlet {

    private final LoginService loginService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginController() {
        this.loginService = new LoginService();
    }

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User userFromFrontend = objectMapper.readValue(request.getReader(), User.class);
        User loggedIn = loginService.login(userFromFrontend.getEmail(), userFromFrontend.getPasswordHash());

        Map<String, Object> jsonResponse = new HashMap<>();
        if (loggedIn == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid email or password.");
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Login successful!");
            jsonResponse.put("role", loggedIn.getRole());
            jsonResponse.put("fullName", loggedIn.getFullName());
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}