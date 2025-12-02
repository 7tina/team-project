package usecase.create_chat;

import java.util.List;

public class CreateChatOutputData {
    private boolean isGroupChat;
    private final String chatId;
    private final String groupName;
    private final List<String> users;
    private final List<String> messageIds;
    private final boolean success;
    private final String message;
    private final String currentUserId;  // NEW: Track the current user

    // Main constructor with currentUserId
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

    // Legacy constructor for backward compatibility (failure cases)
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
        this.currentUserId = null;  // Not provided in legacy calls
    }

    public String getChatId() {return chatId;}

    public String getGroupName() {return groupName;}

    public List<String> getUsers() {return users;}

    public List<String> getMessageIds() {return messageIds;}

    public boolean isSuccess() {return success;}

    public String getMessage() {return message;}

    public boolean isGroupChat() {return isGroupChat;}

    public String getCurrentUserId() {return currentUserId;}
}