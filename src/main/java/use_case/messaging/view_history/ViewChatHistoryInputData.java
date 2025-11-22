package use_case.messaging.view_history;

import java.util.List;

/**
 * Input data for viewing the chat history of a given chat.
 */
public class ViewChatHistoryInputData {

    private final String chatId;

    private final List<String> userIds;

    private final List<String> messageIds;

    public ViewChatHistoryInputData(String chatId,  List<String> userIds, List<String> messageIds) {
        this.chatId = chatId;
        this.userIds = userIds;
        this.messageIds = messageIds;
    }

    public String getChatId() {
        return chatId;
    }

    public List<String> getUserIds() {return userIds;}

    public List<String> getMessageIds() {return messageIds;}
}