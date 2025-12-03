package usecase.recent_chat;

public interface RecentChatsOutputBoundary {

    /**
     * Prepares the view model for a successful retrieval of recent chats.
     *
     * @param outputData the data containing the user's recent chats
     */
    void prepareSuccessView(RecentChatsOutputData outputData);

    /**
     * Prepares the view model for a failed attempt to retrieve recent chats,
     * typically due to missing user data or storage errors.
     *
     * @param errorMessage a message describing why the operation failed
     */
    void prepareFailView(String errorMessage);
}
