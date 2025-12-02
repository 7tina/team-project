package usecase.groups.removeuser;

/**
 * Output data transfer object for the Remove User use case.
 */
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
