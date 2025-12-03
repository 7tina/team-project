package usecase.recent_chat;

public interface RecentChatsInputBoundary {

    /**
     * Executes the Recent Chats use case.
     *
     * @param recentChatsInputData the data required to retrieve recent chats
     */
    void execute(RecentChatsInputData recentChatsInputData);
}
