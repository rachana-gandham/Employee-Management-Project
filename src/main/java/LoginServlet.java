import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get username and password from form input
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Debugging output
        System.out.println("Attempting login for Username: " + username + ", Password: " + password);

        // Database connection
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            // Establish connection to the MySQL database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employees", "root", "bhanuteja");

            // Check for admin credentials first
            if ("admin".equals(username) && "password123".equals(password)) {
                System.out.println("Login successful for admin: " + username);
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                response.sendRedirect("manage.html"); // Redirect to addemployee.html
            } else {
                // Prepare SQL query to check username and password for other users
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                // Check if user exists
                if (rs.next()) {
                    // If login is successful, create a session and redirect to dashboard
                    System.out.println("Login successful for user: " + username);
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    response.sendRedirect("dashboard.html");
                } else {
                    // If login fails, redirect back to login page with error
                    System.out.println("Login failed for user: " + username);
                    response.sendRedirect("error.html");
                }
            }

            // Close the connection
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Redirect to login page if there is a database or other error
            response.sendRedirect("error.html");
        }
    }
}
