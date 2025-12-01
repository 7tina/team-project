package entity;

/**
 * Factory for creating {@link User} objects.
 */
public class UserFactory {

    /**
     * Creates a new {@link User} instance with the given name and password.
     *
     * @param name      the user's name
     * @param password  the user's password
     * @return a new {@link User} object
     */
    public User create(String name, String password) {
        return new User(name, password);
    }
}
