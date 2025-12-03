package usecase.messaging.view_history;

/**
 * Presenter boundary for the view chat history use case.
 */
public interface ViewChatHistoryOutputBoundary {

    /**
     * Prepares the view for a successful retrieval of chat messages.
     * @param outputData the data containing the retrieved chat messages
     */
    void prepareSuccessView(ViewChatHistoryOutputData outputData);

    /**
     * Prepares the view when the chat exists but has no messages yet.
     * @param chatId the ID of the chat with no messages
     */
    void prepareNoMessagesView(String chatId);

    /**
     * Prepares the view when the chat does not exist or another error occurs.
     *
     * @param errorMessage a message describing the failure
     */
    void prepareFailView(String errorMessage);
}

