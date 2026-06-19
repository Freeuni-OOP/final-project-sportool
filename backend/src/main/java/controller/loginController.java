@WebServlet("/api/login")
public class loginController extends HttpServlet{

    private final userDao userDao = new userDaoSql();
    private final ObjectMapper objectMapper = new ObjectMapper();


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
