package usecase.messaging.view_history;

public interface ViewChatHistoryOutputBoundary {

    /**
     * Prepares the view model for a successful chat history retrieval.
     *
     * @param outputData the data containing the retrieved chat history
     */
    void prepareSuccessView(ViewChatHistoryOutputData outputData);

    /**
     * Prepares the view model for a failed attempt to view chat history,
     * typically due to the chat not existing or another error.
     *
     * @param errorMessage the reason the view history action failed
     */
    void prepareFailView(String errorMessage);
}
