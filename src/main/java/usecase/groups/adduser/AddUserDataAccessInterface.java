package usecase.groups.adduser;

import entity.Chat;

/**
 * Data access interface for adding users to group chats.
 * Provides methods to retrieve user information and persist chat modifications.
 */
public interface AddUserDataAccessInterface {

    /**
     * Retrieves the user ID associated with the given username.
     *
     * @param username the username to look up
     * @return the user ID corresponding to the username, or null if the user does not exist
     */
    String getUserIdByUsername(String username);

    /**
     * Adds a user to the specified chat.
     *
     * @param chatId the ID of the chat to add the user to
     * @param userId the ID of the user to add
     */
    void addUser(String chatId, String userId);

    /**
     * Saves the chat entity to persistent storage.
     *
     * @param chat the chat entity to save
     * @return the saved chat entity
     */
    Chat saveChat(Chat chat);
}
