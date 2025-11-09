package feedbacksystem;

public class User {
    public String username;
    public String password;
    public String role;
    public String displayName;

    public User() {}

    public User(String username, String password, String role, String displayName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.displayName = displayName;
    }
}
