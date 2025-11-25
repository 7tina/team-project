package use_case.messaging.search_history;

import entity.Message;

import java.util.List;

/**
 * Output data for the SearchChatHistory use case.
 */
public class SearchChatHistoryOutputData {

    private final List<Message> messages;

    public SearchChatHistoryOutputData(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
