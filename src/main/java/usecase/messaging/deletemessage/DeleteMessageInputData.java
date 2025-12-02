package usecase.messaging.deletemessage;

/**
 * Input data for the delete message use case.
 * <p>
 * Contains the message ID to delete and the user who requested deletion.
 */
public class DeleteMessageInputData {

    private final String messageId;
    private final String currentUserId;

    /**
     * Constructs a {@code DeleteMessageInputData} object.
     *
     * @param messageId     ID of the message to delete
     * @param currentUserId ID of the user requesting the deletion
     */
    public DeleteMessageInputData(String messageId, String currentUserId) {
        this.messageId = messageId;
        this.currentUserId = currentUserId;
    }

    /**
     * Returns the ID of the message to delete.
     *
     * @return message ID
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Returns the ID of the user performing the deletion.
     *
     * @return user ID
     */
    public String getCurrentUserId() {
        return currentUserId;
    }
}