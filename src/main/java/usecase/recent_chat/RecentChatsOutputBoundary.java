package usecase.recent_chat;

public interface RecentChatsOutputBoundary {

    /**
     * Prepares the success view when recent chats are successfully retrieved.
     * @param outputData the data containing the user's recent chats
     */
    void prepareSuccessView(RecentChatsOutputData outputData);

    /**
     * Prepares the failure view when the recent chats retrieval fails.
     *
     * @param errorMessage a message describing the reason for failure
     */
    void prepareFailView(String errorMessage);
}
