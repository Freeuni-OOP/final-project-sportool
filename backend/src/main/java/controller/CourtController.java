package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Court;
import service.CourtService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CourtController extends HttpServlet {
    private CourtService courtService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.courtService = new CourtService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sportType = request.getParameter("type");

        List<Court> courts = courtService.getCourts(sportType);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String jsonResponse = objectMapper.writeValueAsString(courts);
        out.print(jsonResponse);
        out.flush();
    }
}
