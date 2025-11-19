package use_case.messaging.view_history;

/**
 * Input data for viewing the chat history of a given chat.
 */
public class ViewChatHistoryInputData {

    private final String chatId;

    public ViewChatHistoryInputData(String chatId) {
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }
}