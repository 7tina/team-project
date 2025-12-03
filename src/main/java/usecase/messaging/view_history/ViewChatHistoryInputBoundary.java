package usecase.messaging.view_history;

public interface ViewChatHistoryInputBoundary {

    /**
     * Executes the use case to retrieve the message history for a chat.
     * @param inputData the input data required to fetch the chat history,
     *                  such as the chat ID and any relevant filters
     */
    void execute(ViewChatHistoryInputData inputData);
}
