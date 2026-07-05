package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Post;
import service.PostService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/posts")
public class PostController extends HttpServlet {
    private PostService postService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.postService = new PostService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Post> posts = postService.getAllPosts();
        objectMapper.writeValue(response.getWriter(), posts);
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

            Post newPost = objectMapper.readValue(request.getReader(), Post.class);
            newPost.setUserId(authenticatedUserId);

            int postId = postService.createPost(newPost);
            if (postId > 0) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Post created successfully!");
                jsonResponse.put("postId", postId);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to create post. Title and content are required.");
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid request format.");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Server error while creating post.");
        }

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}
