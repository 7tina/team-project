package usecase.recent_chat;

public interface RecentChatsUserDataAccessInterface {

    /**
     * Updates the chat repository for the given user.
     * This may involve refreshing recent chat metadata or ordering chats by last activity.
     *
     * @param userId the ID of the user whose chat repository should be updated
     */
    void updateChatRepository(String userId);
}
