package taxsystem.domain;

public class User {

    private String username;
    private String passwordHash;
    private String email;

    public User(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    public String getUsername()             { return username; }
    public void   setUsername(String u)     { this.username = u; }

    public String getPasswordHash()         { return passwordHash; }
    public void   setPasswordHash(String h) { this.passwordHash = h; }

    public String getEmail()                { return email; }
    public void   setEmail(String e)        { this.email = e; }
}
