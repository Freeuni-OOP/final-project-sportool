package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MatchAnnouncement;
import service.MatchAnnouncementService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/matches/*")
public class MatchAnnouncementController extends HttpServlet {
    private MatchAnnouncementService matchAnnouncementService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.matchAnnouncementService = new MatchAnnouncementService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Integer authenticatedUserId = (Integer) request.getAttribute("authenticatedUserId");
        if (authenticatedUserId == null) {
            authenticatedUserId = 0;
        }

        List<MatchAnnouncement> matches = matchAnnouncementService.getAllMatches(authenticatedUserId);
        objectMapper.writeValue(response.getWriter(), matches);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            Integer authenticatedUserId = (Integer) request.getAttribute("authenticatedUserId");
            String authenticatedRole = (String) request.getAttribute("authenticatedRole");
            if (authenticatedUserId == null || authenticatedUserId <= 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Authentication required.");
                objectMapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }

            if (!"PLAYER".equals(authenticatedRole)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Only players can create or join match announcements.");
                objectMapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }

            if (isJoinRequest(request)) {
                handleJoin(request, response, authenticatedUserId, authenticatedRole, jsonResponse);
                return;
            }

            MatchAnnouncement match = objectMapper.readValue(request.getReader(), MatchAnnouncement.class);
            match.setUserId(authenticatedUserId);

            int matchId = matchAnnouncementService.createMatch(match);
            if (matchId > 0) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Match announcement created successfully.");
                jsonResponse.put("matchId", matchId);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Venue, date/time, sport type, and players needed are required.");
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid request format.");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid date/time format.");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Server error while saving match announcement.");
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }

    private boolean isJoinRequest(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return pathInfo != null && pathInfo.equals("/join");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            Integer authenticatedUserId = (Integer) request.getAttribute("authenticatedUserId");
            if (authenticatedUserId == null || authenticatedUserId <= 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Authentication required.");
                objectMapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }

            String matchIdStr = request.getParameter("id");
            if (matchIdStr == null || matchIdStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Match ID is required.");
                objectMapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }

            int matchId = Integer.parseInt(matchIdStr);
            if (matchAnnouncementService.deleteMatch(matchId, authenticatedUserId)) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Match announcement deleted successfully.");
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "You cannot delete this match announcement or it does not exist.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid match ID.");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Server error while deleting match announcement.");
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }

    private void handleJoin(HttpServletRequest request, HttpServletResponse response, int userId,
                            String role, Map<String, Object> jsonResponse) throws IOException {
        if (!"PLAYER".equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Only players can join match announcements.");
            objectMapper.writeValue(response.getWriter(), jsonResponse);
            return;
        }

        try {
            String matchIdStr = request.getParameter("id");
            if (matchIdStr == null || matchIdStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Match ID is required.");
                objectMapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }

            int matchId = Integer.parseInt(matchIdStr);
            if (matchAnnouncementService.joinMatch(matchId, userId)) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.put("success", true);
                jsonResponse.put("message", "You joined this match.");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Could not join this match.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid match ID.");
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}
