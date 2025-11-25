package interface_adapter.messaging.search_history;

import use_case.messaging.search_history.SearchChatHistoryInputBoundary;
import use_case.messaging.search_history.SearchChatHistoryInputData;

public class SearchChatHistoryController {

    private final SearchChatHistoryInputBoundary interactor;

    public SearchChatHistoryController(SearchChatHistoryInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String chatId, String keyword) {
        SearchChatHistoryInputData input =
                new SearchChatHistoryInputData(chatId, keyword);
        interactor.execute(input);
    }
}
