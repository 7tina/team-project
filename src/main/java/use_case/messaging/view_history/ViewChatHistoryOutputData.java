package use_case.messaging.view_history;

import java.util.List;
import java.util.Map;

/**
 * Output data for the view chat history use case.
 */
public class ViewChatHistoryOutputData {

    private final String chatId;
    private final List<String[]> messages;
    private final Map<String, Map<String, String>> reactions;

    public ViewChatHistoryOutputData(String chatId, List<String[]> messages,
                                     Map<String, Map<String, String>> reactions) {
        this.chatId = chatId;
        this.messages = messages;
        this.reactions = reactions;
    }

    public String getChatId() {
        return chatId;
    }

    public List<String[]> getMessages() {
        return messages;
    }

    public Map<String, Map<String, String>> getReactions() { return reactions; }
}

