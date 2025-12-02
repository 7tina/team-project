package usecase.messaging.deletemessage;

import java.time.LocalDateTime;

/**
 * Output data for the delete message use case.
 * <p>
 * Contains the ID of the deleted message, the deletion timestamp,
 * a success flag, and an optional failure reason.
 */
public class DeleteMessageOutputData {

    private final String messageId;
    private final LocalDateTime deletionTime;
    private final boolean success;
    private final String failReason;

    /**
     * Full constructor for a delete message result.
     *
     * @param messageId    ID of the deleted message
     * @param deletionTime time when the deletion occurred
     * @param success      whether deletion succeeded
     * @param failReason   reason for failure, or {@code null} if successful
     */
    public DeleteMessageOutputData(String messageId,
                                   LocalDateTime deletionTime,
                                   boolean success,
                                   String failReason) {
        this.messageId = messageId;
        this.deletionTime = deletionTime;
        this.success = success;
        this.failReason = failReason;
    }

    /**
     * Convenience constructor for a successful deletion with no failure reason.
     *
     * @param messageId    ID of the deleted message
     * @param deletionTime time when the deletion occurred
     */
    public DeleteMessageOutputData(String messageId, LocalDateTime deletionTime) {
        this(messageId, deletionTime, true, null);
    }

    /**
     * Returns the ID of the deleted message.
     *
     * @return deleted message ID
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Returns the deletion timestamp.
     *
     * @return time of deletion
     */
    public LocalDateTime getDeletionTime() {
        return deletionTime;
    }

    /**
     * Whether the deletion succeeded.
     *
     * @return {@code true} if success, {@code false} otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * For failed deletions, returns the failure reason.
     *
     * @return failure reason, or {@code null} if the deletion succeeded
     */
    public String getFailReason() {
        return failReason;
    }
}