package use_case.groups.adduser;

public class AddUserInputData {
    private final String chatId;
    private final String usernameToAdd;

    public AddUserInputData(String chatId, String usernameToAdd) {
        this.chatId = chatId;
        this.usernameToAdd = usernameToAdd;
    }

    public String getChatId() {
        return chatId;
    }

    public String getUsernameToAdd() {
        return usernameToAdd;
    }
}