package usecase.accesschat;

public interface AccessChatDataAccessInterface {

    /**
     * Updates the chat repository to reflect that the specified user
     * is no longer active. Implementations may use this method to
     * clear cached chat data, remove active chat sessions, or perform
     * other cleanup operations associated with the user.
     * @param userId the unique identifier of the user whose chat data
     *               should be updated
     */
    void updateChatRepository(String userId);
}
