package usecase.messaging.search_history;

/**
 * Input boundary (interactor interface) for the Search Chat History use case.
 * Defines the method that the controller will call to trigger the search.
 */
public interface SearchChatHistoryInputBoundary {

    /**
     * Executes the search chat history use case with the given input data.
     *
     * @param inputData the data required to perform the search
     */
    void execute(SearchChatHistoryInputData inputData);
}
