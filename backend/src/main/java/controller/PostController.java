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
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/posts")
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

        String jsonResponse = objectMapper.writeValueAsString(posts);
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {

            Post newPost = objectMapper.readValue(request.getReader(), Post.class);

            boolean isCreated = postService.createPost(newPost);

            if (isCreated) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"message\": \"Post created successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Failed to create post. Invalid data.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}
