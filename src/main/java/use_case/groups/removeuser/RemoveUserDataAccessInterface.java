package use_case.groups.removeuser;

import entity.Chat;

public interface RemoveUserDataAccessInterface {
    /**
     * Saves the updated chat.
     * @param chat the chat to save
     */
    Chat saveChat(Chat chat);

    /**
     * Gets a user ID by their username.
     * @param username the username
     * @return the user ID, or null if not found
     */
    String getUserIdByUsername(String username);

    void removeUser(String chatId, String userId);
}