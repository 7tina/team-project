package usecase.messaging.searchhistory;

/**
 * Output Boundary for the Search Chat History use case.
 * Defines how the interactor communicates results to the presenter layer.
 */
public interface SearchChatHistoryOutputBoundary {

    /**
     * Prepares the view for a successful search.
     *
     * @param outputData the output data containing the matching messages
     */
    void prepareSuccessView(SearchChatHistoryOutputData outputData);

    /**
     * Prepares the view when no messages match the search keyword.
     *
     * @param chatId  the id of the chat that was searched
     * @param keyword the keyword used for the search
     */
    void prepareNoMatchesView(String chatId, String keyword);

    /**
     * Prepares the view when the search fails.
     *
     * @param errorMessage the error message to be shown to the user
     */
    void prepareFailView(String errorMessage);
}
