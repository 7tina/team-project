package entity;

/**
 * A simple entity representing a user. Users have a username and password..
 */
public class User {

    private final String name;
    private String password;

    /**
     * Creates a new user with the given non-empty name and non-empty password.
     * @param name the username
     * @param password the password
     * @throws IllegalArgumentException if the email, password, or name are empty
     */
    public User(String name, String password) {
        if ("".equals(name)) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if ("".equals(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String passwordHash) {
        this.password = passwordHash;
    }

}
