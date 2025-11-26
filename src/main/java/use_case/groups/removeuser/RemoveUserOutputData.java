package use_case.groups.removeuser;

public class RemoveUserOutputData {
    private final String chatId;
    private final String removedUsername;

    public RemoveUserOutputData(String chatId, String removedUsername) {
        this.chatId = chatId;
        this.removedUsername = removedUsername;
    }

    public String getChatId() {
        return chatId;
    }

    public String getRemovedUsername() {
        return removedUsername;
    }
}