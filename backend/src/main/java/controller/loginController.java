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
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/login")
public class loginController extends HttpServlet{

    private final userDao userDao;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public loginController() {
        this.userDao = new userDaoSql();
    }

    public loginController(userDao userDao) {
        this.userDao = userDao;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        user userFromFrontend = objectMapper.readValue(request.getReader(), user.class);
        String email = userFromFrontend.getEmail();
        String password = userFromFrontend.getPasswordHash();

        user foundUser = userDao.getUserByEmail(email);

        Map<String, Object> jsonResponse = new HashMap<>();

        if(foundUser == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("success", false);
            jsonResponse.put("message","Invalid email or pasword.");
            objectMapper.writeValue(response.getWriter(),jsonResponse);
            return;
        }

        boolean passwordMatch = BCrypt.checkpw(password,foundUser.getPasswordHash());

        if(passwordMatch){
            response.setStatus(HttpServletResponse.SC_OK);
            jsonResponse.put("success",true);
            jsonResponse.put("message","Login successful!");
            jsonResponse.put("role",foundUser.getRole());
            jsonResponse.put("fullName",foundUser.getFullName());
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid email or password.");
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
      }
    }
