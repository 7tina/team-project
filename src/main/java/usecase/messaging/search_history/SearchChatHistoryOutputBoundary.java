package usecase.messaging.search_history;

public interface SearchChatHistoryOutputBoundary {

    void prepareSuccessView(SearchChatHistoryOutputData outputData);

    void prepareNoMatchesView(String chatId, String keyword);

    void prepareFailView(String errorMessage);
}
