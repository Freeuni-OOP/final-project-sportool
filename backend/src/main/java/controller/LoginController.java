package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.LoginService;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/login")
public class LoginController extends HttpServlet {

    private final LoginService loginService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Key SIGNING_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

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
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            Date expiryDate = new Date(nowMillis + (24 * 60 * 60 * 1000));

            String jwtToken = Jwts.builder()
                    .setSubject(loggedIn.getEmail())
                    .claim("role", loggedIn.getRole())
                    .claim("fullName", loggedIn.getFullName())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(SIGNING_KEY)
                    .compact();

            Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);

            response.addCookie(jwtCookie);

            response.setStatus(HttpServletResponse.SC_OK);
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Login successful!");
            jsonResponse.put("role", loggedIn.getRole());
            jsonResponse.put("fullName", loggedIn.getFullName());
            jsonResponse.put("userId", loggedIn.getId());
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}