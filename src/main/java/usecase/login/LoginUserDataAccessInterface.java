package usecase.login;

import entity.User;

/**
 * DAO interface for the Login Use Case.
 */
public interface LoginUserDataAccessInterface {

    /**
     * Checks if the given username exists.
     * @param username the username to look for
     * @return true if a user with the given username exists; false otherwise
     */
    boolean existsByName(String username);

    /**
     * Saves the user.
     * @param user the user to save
     */
    void save(User user);

    /**
     * Returns the user with the given username.
     * @param username the username to look up
     * @return the user with the given username
     */
    User get(String username);

    /**
     * Sets the current username for the session or user context.
     * @param name the username to be set as the current user
     */
    void setCurrentUsername(String name);

    /**
     * Retrieves the current username for the session or user context.
     * @return the username of the current user, or null if no user is set
     */
    String getCurrentUsername();
}
