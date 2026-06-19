package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.userDao;
import dao.userDaoSql;
import model.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/register")
public class registerController extends HttpServlet {

    private final userDao userDao = new userDaoSql();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        user userFromFrontend = objectMapper.readValue(request.getReader(), user.class);

        boolean isSuccess = userDao.registerUser(userFromFrontend);

        Map<String, Object> jsonResponse = new HashMap<>();
        if (isSuccess) {
            response.setStatus(HttpServletResponse.SC_CREATED); // Status 201
            jsonResponse.put("success", true);
            jsonResponse.put("message", "User registered successfully!");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Status 400
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Registration failed. Email might already exist.");
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}