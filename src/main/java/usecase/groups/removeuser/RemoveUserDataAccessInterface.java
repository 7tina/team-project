package usecase.groups.removeuser;

import entity.Chat;

/**
 * Data access interface for removing users from group chats.
 * Provides methods to retrieve user information, remove users, and persist chat modifications.
 */
public interface RemoveUserDataAccessInterface {

    /**
     * Saves the chat entity to persistent storage.
     *
     * @param chat the chat entity to save
     * @return the saved chat entity
     */
    Chat saveChat(Chat chat);

    /**
     * Retrieves the user ID associated with the given username.
     *
     * @param username the username to look up
     * @return the user ID corresponding to the username, or null if the user does not exist
     */
    String getUserIdByUsername(String username);

    /**
     * Removes a user from the specified chat.
     *
     * @param chatId the ID of the chat to remove the user from
     * @param userId the ID of the user to remove
     */
    void removeUser(String chatId, String userId);
}
