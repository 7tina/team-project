package usecase.groups.adduser;

/**
 * Output data transfer object for the Add User use case.
 * Encapsulates the result information after successfully adding a user to a group chat.
 */
public class AddUserOutputData {
    private final String chatId;
    private final String addedUsername;

    /**
     * Constructs an AddUserOutputData with the specified chat ID and added username.
     *
     * @param chatId the ID of the chat where the user was added
     * @param addedUsername the username of the user that was added to the chat
     */
    public AddUserOutputData(String chatId, String addedUsername) {
        this.chatId = chatId;
        this.addedUsername = addedUsername;
    }

    /**
     * Gets the chat ID.
     *
     * @return the ID of the chat
     */
    public String getChatId() {
        return chatId;
    }

    /**
     * Gets the added username.
     *
     * @return the username of the user that was added
     */
    public String getAddedUsername() {
        return addedUsername;
    }
}
