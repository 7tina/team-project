package usecase.messaging.deletemessage;

/**
 * Data access interface for the delete message use case.
 * <p>
 * Implementations are responsible for removing a message from persistent storage.
 */
public interface DeleteMessageDataAccessInterface {

    /**
     * Deletes a message with the given ID.
     *
     * @param messageId ID of the message to delete
     */
    void deleteMessageById(String messageId);
}
