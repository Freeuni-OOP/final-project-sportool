package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.TrainerDao;
import model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.TrainerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TrainerControllerTest {

    private TrainerController controller;
    private TrainerDao mockTrainerDao;
    private TrainerService trainerService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("net.bytebuddy.experimental", "true");
        mockTrainerDao = Mockito.mock(TrainerDao.class);
        trainerService = new TrainerService(mockTrainerDao);
        controller = new TrainerController(trainerService);

        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllTrainers() throws Exception {
        Trainer t1 = new Trainer(1, 1, "Dato", "Okmelashvili",
                "599123456", "Football", 50.0, 4.5, 10);
        Trainer t2 = new Trainer(2, 2, "Nika", "Dodashvili",
                "577123456", "Basketball", 40.0, 3.8, 5);

        when(mockTrainerDao.getAllTrainers()).thenReturn(Arrays.asList(t1, t2));
        when(request.getPathInfo()).thenReturn(null);

        controller.doGet(request, response);

        verify(response).setContentType("application/json");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> result = objectMapper.readValue(responseWriter.toString(), List.class);
        assertEquals(2, result.size());
    }

    @Test
    void testGetTrainerByIdSuccess() throws Exception {
        Trainer trainer = new Trainer(1, 1, "Dato", "Okmelashvili",
                "599123456", "Football", 50.0, 4.5, 10);

        when(mockTrainerDao.getTrainerById(1)).thenReturn(trainer);
        when(request.getPathInfo()).thenReturn("/1");

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = objectMapper.readValue(responseWriter.toString(), Map.class);
        assertEquals("Dato", result.get("firstName"));
    }

    @Test
    void testGetTrainerByIdNotFound() throws Exception {
        when(mockTrainerDao.getTrainerById(999)).thenReturn(null);
        when(request.getPathInfo()).thenReturn("/999");

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = objectMapper.readValue(responseWriter.toString(), Map.class);
        assertEquals(false, result.get("success"));
    }

    @Test
    void testAddTrainerSuccess() throws Exception {
        Trainer trainer = new Trainer(0, 1, "Dato", "Okmelashvili",
                "599123456", "Football", 50.0, 0.0, 0);

        when(mockTrainerDao.addTrainer(any())).thenReturn(true);

        String json = "{\"userId\":1,\"firstName\":\"Dato\",\"lastName\":\"Okmelashvili\"," +
                "\"phone\":\"599123456\",\"sportType\":\"Football\",\"pricePerSession\":50.0}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = objectMapper.readValue(responseWriter.toString(), Map.class);
        assertEquals(true, result.get("success"));
    }

    @Test
    void testAddTrainerFails() throws Exception {
        when(mockTrainerDao.addTrainer(any())).thenReturn(false);

        String json = "{\"userId\":1,\"firstName\":\"\",\"lastName\":\"Okmelashvili\"," +
                "\"phone\":\"599123456\",\"sportType\":\"Football\",\"pricePerSession\":50.0}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = objectMapper.readValue(responseWriter.toString(), Map.class);
        assertEquals(false, result.get("success"));
    }
}