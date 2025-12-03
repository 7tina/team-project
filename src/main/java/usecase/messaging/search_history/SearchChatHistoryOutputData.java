package usecase.messaging.search_history;

import java.util.List;

import entity.Message;

public class SearchChatHistoryOutputData {
    private final List<Message> matchingMessages;

    public SearchChatHistoryOutputData(List<Message> matchingMessages) {
        this.matchingMessages = matchingMessages;
    }

    public List<Message> getMatchingMessages() {
        return matchingMessages;
    }

    public List<Message> getMessages() {
        return matchingMessages;
    }
}
