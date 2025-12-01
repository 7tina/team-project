package interface_adapter.messaging.search_history;

import use_case.messaging.search_history.SearchChatHistoryInputBoundary;
import use_case.messaging.search_history.SearchChatHistoryInputData;

public class SearchChatHistoryController {
    private final SearchChatHistoryInputBoundary interactor;

    public SearchChatHistoryController(SearchChatHistoryInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes a search in the chat history for the given chat ID and keyword.
     *
     * @param chatId  the ID of the chat to search in
     * @param keyword the keyword used for searching the chat history
     */
    public void execute(String chatId, String keyword) {
        final SearchChatHistoryInputData inputData =
                new SearchChatHistoryInputData(chatId, keyword);
        interactor.execute(inputData);
    }
}
