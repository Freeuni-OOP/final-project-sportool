package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.userDao;
import model.user;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class loginControllerTest {

    private loginController controller;
    private userDao mockUserDao;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        mockUserDao = Mockito.mock(userDao.class);
        controller = new loginController(mockUserDao);

        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        objectMapper = new ObjectMapper();
    }

    @Test
    void testLoginSuccess() throws Exception {
        String hashedPassword = BCrypt.hashpw("1234", BCrypt.gensalt());

        String json = "{\"email\":\"test@test.com\",\"password\":\"1234\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        user fakeUser = new user(1, "test@test.com", hashedPassword, "PLAYER", "Test User");
        when(mockUserDao.getUserByEmail("test@test.com")).thenReturn(fakeUser);

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = objectMapper.readValue(responseWriter.toString(), Map.class);
        assertEquals(true, result.get("success"));
        assertEquals("Login successful!", result.get("message"));
    }

    @Test
    void testLoginUserNotFound() throws Exception {
        String json = "{\"email\":\"nobody@test.com\",\"password\":\"1234\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
        when(mockUserDao.getUserByEmail("nobody@test.com")).thenReturn(null);

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = objectMapper.readValue(responseWriter.toString(), Map.class);
        assertEquals(false, result.get("success"));
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        String hashedPassword = BCrypt.hashpw("1234", BCrypt.gensalt());

        String json = "{\"email\":\"test@test.com\",\"password\":\"wrongpassword\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        user fakeUser = new user(1, "test@test.com", hashedPassword, "PLAYER", "Test User");
        when(mockUserDao.getUserByEmail("test@test.com")).thenReturn(fakeUser);

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = objectMapper.readValue(responseWriter.toString(), Map.class);
        assertEquals(false, result.get("success"));
    }
}