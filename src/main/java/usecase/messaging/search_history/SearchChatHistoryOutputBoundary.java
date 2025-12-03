package usecase.messaging.search_history;

/**
 * Output boundary for the Search Chat History use case.
 * Defines how the interactor communicates results back to the presenter.
 */
public interface SearchChatHistoryOutputBoundary {

    /**
     * Prepares the view model for a successful search.
     *
     * @param outputData the data containing search results
     */
    void prepareSuccessView(SearchChatHistoryOutputData outputData);

    /**
     * Prepares the view model when no messages match the keyword.
     *
     * @param chatId  the ID of the chat where the search was conducted
     * @param keyword the keyword that was searched
     */
    void prepareNoMatchesView(String chatId, String keyword);

    /**
     * Prepares the view model for a failed search,
     * typically due to invalid input or system errors.
     *
     * @param errorMessage the error message explaining the reason for failure
     */
    void prepareFailView(String errorMessage);
}
