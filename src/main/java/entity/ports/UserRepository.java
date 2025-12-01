package entity.ports;

import java.util.Optional;

import entity.User;

/**
 * Repository interface for managing users.
 */
public interface UserRepository {

    /**
     * Finds a user by username.
     *
     * @param username the username of the user
     * @return an Optional containing the user if found, otherwise empty
     */
    Optional<User> findByUsername(String username);

    /**
     * Saves the given user.
     *
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);
}
