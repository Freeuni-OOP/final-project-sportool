package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Comment;
import service.CommentService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/comments")
public class CommentController extends HttpServlet {
    private CommentService commentService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.commentService = new CommentService();
        this.objectMapper = new ObjectMapper();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String postIdStr = request.getParameter("postId");
        if (postIdStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int postId = Integer.parseInt(postIdStr);
        List<Comment> comments = commentService.getCommentsForPost(postId);
        objectMapper.writeValue(response.getWriter(), comments);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

            Comment newComment = objectMapper.readValue(request.getReader(), Comment.class);
            newComment.setUserId(authenticatedUserId);

            if (commentService.addComment(newComment)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Comment added successfully!");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to add comment. Content cannot be empty.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Server error.");
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
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

            String commentIdStr = request.getParameter("id");
            if (commentIdStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Comment ID is required.");
                objectMapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }

            int commentId = Integer.parseInt(commentIdStr);

            if (commentService.removeComment(commentId, authenticatedUserId)) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Comment deleted successfully!");
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "You cannot delete this comment or it doesn't exist.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Server error.");
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}