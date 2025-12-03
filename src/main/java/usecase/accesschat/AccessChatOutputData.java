package usecase.accesschat;

import java.util.List;

public class AccessChatOutputData {
    private final boolean isGroupChat;
    private final String chatId;
    private final String groupName;
    private final List<String> users;
    private final List<String> messageIds;
    private final String currentUserId;

    public AccessChatOutputData(boolean isGroupChat, String chatId,
                                String groupName, List<String> users,
                                List<String> messageIds, String currentUserId) {
        this.isGroupChat = isGroupChat;
        this.chatId = chatId;
        this.groupName = groupName;
        this.users = users;
        this.messageIds = messageIds;
        this.currentUserId = currentUserId;
    }

    public boolean isGroupChat() {
        return isGroupChat;
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

    public String getCurrentUserId() {
        return currentUserId;
    }
}
