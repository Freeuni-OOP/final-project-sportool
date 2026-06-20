package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.UserDao;
import dao.UserDaoSql;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@WebServlet("/api/register")
public class RegisterController extends HttpServlet {

    private final UserDao userDao = new UserDaoSql();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User userFromFrontend = objectMapper.readValue(request.getReader(), User.class);

        Set<ConstraintViolation<User>> violations = validator.validate(userFromFrontend);

        Map<String, Object> jsonResponse = new HashMap<>();

        if (!violations.isEmpty()) {
            String errorMessage = violations.iterator().next().getMessage();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", errorMessage);
            objectMapper.writeValue(response.getWriter(), jsonResponse);
            return;
        }

        boolean isSuccess = userDao.registerUser(userFromFrontend);

        if (isSuccess) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            jsonResponse.put("success", true);
            jsonResponse.put("message", "User registered successfully!");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Registration failed. Email might already exist.");
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}