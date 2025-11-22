package use_case.messaging.view_history;

import java.util.List;

/**
 * Output data for the view chat history use case.
 */
public class ViewChatHistoryOutputData {

    private final String chatId;
    private final List<String[]> messages;

    public ViewChatHistoryOutputData(String chatId, List<String[]> messages) {
        this.chatId = chatId;
        this.messages = messages;
    }

    public String getChatId() {
        return chatId;
    }

    public List<String[]> getMessages() {
        return messages;
    }
}

