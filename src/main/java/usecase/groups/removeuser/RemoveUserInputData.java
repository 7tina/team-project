package usecase.groups.removeuser;

/**
 * Input data transfer object for the Remove User use case.
 */
public class RemoveUserInputData {
    private final String chatId;
    private final String usernameToRemove;
    private final String currentUserId;

    public RemoveUserInputData(String chatId, String usernameToRemove, String currentUserId) {
        this.chatId = chatId;
        this.usernameToRemove = usernameToRemove;
        this.currentUserId = currentUserId;
    }

    public String getChatId() {
        return chatId;
    }

    public String getUsernameToRemove() {
        return usernameToRemove;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }
}
