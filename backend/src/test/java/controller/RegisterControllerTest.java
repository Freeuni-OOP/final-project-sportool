package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.UserDao;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
//import org.reflectasm.AccessClassLoader;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class RegisterControllerTest {

    @InjectMocks
    private RegisterController controller;

    @Mock
    private UserDao userDao;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        Field field = RegisterController.class.getDeclaredField("userDao");
        field.setAccessible(true);
        field.set(controller, userDao);
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        User validUser = new User();
        validUser.setEmail("test@freeuni.edu.ge");
        validUser.setPasswordHash("secure123");
        validUser.setFullName("Giorgi Giorgadze");

        String json = objectMapper.writeValueAsString(validUser);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
        when(userDao.registerUser(any(User.class))).thenReturn(true);

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        String result = responseWriter.toString();
        assertTrue(result.contains("\"success\":true"));
    }

    @Test
    public void testRegisterValidationFailed() throws Exception {
        User invalidUser = new User();
        invalidUser.setEmail("test@freeuni.edu.ge");
        invalidUser.setPasswordHash("123");
        invalidUser.setFullName("Giorgi Giorgadze");

        String json = objectMapper.writeValueAsString(invalidUser);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String result = responseWriter.toString();
        assertTrue(result.contains("\"success\":false"));
        assertTrue(result.contains("Password must be at least 6 characters long"));
    }
}