package usecase.create_chat;

import entity.Chat;

/**
 * Interface for data access operations in create chat use case.
 */
public interface CreateChatUserDataAccessInterface {
    /**
     * Loads a user entity by username.
     *
     * @param username the username to load
     * @return true if user was successfully loaded, false otherwise
     */
    boolean loadToEntity(String username);

    /**
     * Updates the chat repository for a given user.
     *
     * @param username the username whose chat repository should be updated
     */
    void updateChatRepository(String username);

    /**
     * Saves a chat to the data store.
     *
     * @param chat the chat to save
     */
    void saveChat(Chat chat);
}
