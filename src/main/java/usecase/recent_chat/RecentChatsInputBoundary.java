package usecase.recent_chat;

public interface RecentChatsInputBoundary {

    /**
     * Executes the recent chats use case using the provided input data.
     * @param recentChatsInputData the data required to fetch and process
     *                             the user's recent chats
     */
    void execute(RecentChatsInputData recentChatsInputData);
}
