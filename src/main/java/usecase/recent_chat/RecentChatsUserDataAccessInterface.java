package usecase.recent_chat;

public interface RecentChatsUserDataAccessInterface {

    /**
     * Updates the chat repository associated with the specified user.
     * @param userId the unique identifier of the user whose chat data
     *               needs to be updated
     */
    void updateChatRepository(String userId);
}
