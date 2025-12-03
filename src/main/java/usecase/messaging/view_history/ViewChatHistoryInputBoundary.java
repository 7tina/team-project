package usecase.messaging.view_history;

public interface ViewChatHistoryInputBoundary {

    /**
     * Executes the View Chat History use case.
     *
     * @param inputData the data required to perform the chat history retrieval
     */
    void execute(ViewChatHistoryInputData inputData);
}
