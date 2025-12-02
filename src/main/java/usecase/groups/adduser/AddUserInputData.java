package usecase.groups.adduser;

/**
 * Input data transfer object for the Add User use case.
 * Encapsulates the information needed to add a user to a group chat.
 */
public class AddUserInputData {
    private final String chatId;
    private final String usernameToAdd;

    /**
     * Constructs an AddUserInputData with the specified chat ID and username.
     *
     * @param chatId the ID of the chat to add the user to
     * @param usernameToAdd the username of the user to be added
     */
    public AddUserInputData(String chatId, String usernameToAdd) {
        this.chatId = chatId;
        this.usernameToAdd = usernameToAdd;
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
     * Gets the username to add.
     *
     * @return the username of the user to be added to the chat
     */
    public String getUsernameToAdd() {
        return usernameToAdd;
    }
}
