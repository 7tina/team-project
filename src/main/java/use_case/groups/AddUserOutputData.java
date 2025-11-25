package use_case.groups;

public class AddUserOutputData {
    private final String chatId;
    private final String addedUsername;

    public AddUserOutputData(String chatId, String addedUsername) {
        this.chatId = chatId;
        this.addedUsername = addedUsername;
    }

    public String getChatId() {
        return chatId;
    }

    public String getAddedUsername() {
        return addedUsername;
    }
}