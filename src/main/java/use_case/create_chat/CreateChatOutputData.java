package use_case.create_chat;

import java.util.List;

public class CreateChatOutputData {
    private final String chatId;
    private final String groupName;
    private final List<String> users;
    private final List<String> messageIds;
    private final boolean success;
    private final String message;

    public CreateChatOutputData(String chatId, String name,
                                List<String> users, List<String> messageIds,
                                boolean success, String message) {
        this.chatId = chatId;
        this.groupName = name;
        this.users = users;
        this.messageIds = messageIds;
        this.success = success;
        this.message = message;
    }

    public String getChatId() {return chatId;}

    public String getGroupName() {return groupName;}

    public List<String> getUsers() {return users;}

    public List<String> getMessageIds() {return messageIds;}

    public boolean isSuccess() {return success;}

    public String getMessage() {return message;}
}
