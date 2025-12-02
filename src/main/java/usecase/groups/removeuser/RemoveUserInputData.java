package usecase.groups.removeuser;

public class RemoveUserInputData {
    private final String chatId;
    private final String usernameToRemove;

    public RemoveUserInputData(String chatId, String usernameToRemove) {
        this.chatId = chatId;
        this.usernameToRemove = usernameToRemove;
    }

    public String getChatId() {
        return chatId;
    }

    public String getUsernameToRemove() {
        return usernameToRemove;
    }
}