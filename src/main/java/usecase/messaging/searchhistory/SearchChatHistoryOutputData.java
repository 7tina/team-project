package usecase.messaging.searchhistory;

import java.util.List;

import entity.Message;

/**
 * Output data for the Search Chat History use case.
 * Contains the list of messages that matched the search keyword.
 */
public class SearchChatHistoryOutputData {

    private final List<Message> matchingMessages;

    /**
     * Constructs a SearchChatHistoryOutputData instance.
     *
     * @param matchingMessages the list of messages that matched the keyword
     */
    public SearchChatHistoryOutputData(final List<Message> matchingMessages) {
        this.matchingMessages = matchingMessages;
    }

    /**
     * Returns the list of matching messages.
     *
     * @return the list of messages that match the search keyword
     */
    public List<Message> getMatchingMessages() {
        return matchingMessages;
    }

    /**
     * Returns the list of messages (alias for getMatchingMessages).
     *
     * @return the list of messages that match the search keyword
     */
    public List<Message> getMessages() {
        return matchingMessages;
    }
}
