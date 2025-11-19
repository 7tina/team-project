package use_case.messaging.view_history;

/**
 * Presenter boundary for the view chat history use case.
 */
public interface ViewChatHistoryOutputBoundary {

    void prepareSuccessView(ViewChatHistoryOutputData outputData);

    /** Called when the chat exists but has no messages yet. */
    void prepareNoMessagesView(String chatId);

    /** Called when the chat doesn't exist or some other error happens. */
    void prepareFailView(String errorMessage);
}

