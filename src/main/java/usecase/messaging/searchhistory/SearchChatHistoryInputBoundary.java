package usecase.messaging.searchhistory;

/**
 * Input Boundary for the Search Chat History use case.
 * Defines the method that the interactor must implement.
 */
public interface SearchChatHistoryInputBoundary {

    /**
     * Executes the search chat history use case.
     *
     * @param inputData the input data containing search parameters
     */
    void execute(SearchChatHistoryInputData inputData);
}
