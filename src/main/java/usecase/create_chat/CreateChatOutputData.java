package usecase.create_chat;

import java.util.List;

/**
 * Output data for creating a chat.
 */
public class CreateChatOutputData {
    private final boolean isGroupChat;
    private final String chatId;
    private final String groupName;
    private final List<String> users;
    private final List<String> messageIds;
    private final boolean success;
    private final String message;
    private final String currentUserId;

    public CreateChatOutputData(boolean isGroupChat, String chatId, String name,
                                List<String> users, List<String> messageIds,
                                boolean success, String message, String currentUserId) {
        this.isGroupChat = isGroupChat;
        this.chatId = chatId;
        this.groupName = name;
        this.users = users;
        this.messageIds = messageIds;
        this.success = success;
        this.message = message;
        this.currentUserId = currentUserId;
    }

    public CreateChatOutputData(boolean isGroupChat, String chatId, String name,
                                List<String> users, List<String> messageIds,
                                boolean success, String message) {
        this.isGroupChat = isGroupChat;
        this.chatId = chatId;
        this.groupName = name;
        this.users = users;
        this.messageIds = messageIds;
        this.success = success;
        this.message = message;
        this.currentUserId = null;
    }

    public String getChatId() {
        return chatId;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<String> getUsers() {
        return users;
    }

    public List<String> getMessageIds() {
        return messageIds;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }
}
