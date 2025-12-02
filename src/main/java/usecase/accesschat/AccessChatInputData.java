package usecase.accesschat;

public class AccessChatInputData {
    private final String userId;
    private final String chatId;

    public AccessChatInputData(String userId, String chatId) {
        this.userId = userId;
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public String getChatId() {
        return chatId;
    }
}
