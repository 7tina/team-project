package usecase.groups.adduser;

/**
 * Output data transfer object for the Add User use case.
 */
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
