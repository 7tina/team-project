package usecase.messaging.viewhistory;

/**
 * Input Boundary for the View Chat History use case.
 * Defines the method that the interactor must implement.
 */
public interface ViewChatHistoryInputBoundary {

    /**
     * Executes the View Chat History use case.
     *
     * @param inputData the input data containing the chat and user information
     */
    void execute(ViewChatHistoryInputData inputData);
}
