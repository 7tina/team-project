package usecase.messaging.viewhistory;

/**
 * Presenter boundary for the view chat history use case.
 */
public interface ViewChatHistoryOutputBoundary {

    /**
     * Called when viewing chat history succeeds.
     *
     * @param outputData the data needed to render the chat history view
     */
    void prepareSuccessView(ViewChatHistoryOutputData outputData);

    /**
     * Called when the chat exists but has no messages yet.
     *
     * @param chatId the id of the chat whose history was requested
     */
    void prepareNoMessagesView(String chatId);

    /**
     * Called when the chat does not exist or some other error happens.
     *
     * @param errorMessage the error message to be shown to the user
     */
    void prepareFailView(String errorMessage);
}
