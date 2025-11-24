package use_case.groups;

import entity.Chat;

public interface RemoveUserDataAccessInterface {
    /**
     * Gets a chat by its ID.
     * @param chatId the chat ID
     * @return the Chat object, or null if not found
     */
    Chat getChat(String chatId);

    /**
     * Saves the updated chat.
     * @param chat the chat to save
     */
    void saveChat(Chat chat);

    /**
     * Gets a user ID by their username.
     * @param username the username
     * @return the user ID, or null if not found
     */
    String getUserIdByUsername(String username);
}