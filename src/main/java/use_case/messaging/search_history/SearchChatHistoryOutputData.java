package use_case.messaging.search_history;

import entity.Message;
import java.util.List;

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
