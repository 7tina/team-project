package usecase.accesschat;

/**
 * Data access interface for the Access Chat use case.
 * Provides methods for updating chat-related data for a given user.
 */
public interface AccessChatDataAccessInterface {

    /**
     * Updates the chat repository for the specified user.
     * This typically refreshes or synchronizes the stored chat data
     * so it reflects the latest state from persistence.
     *
     * @param userId the ID of the user whose chat data should be updated
     */
    void updateChatRepository(String userId);
}
