package usecase.messaging.remove_reaction;

/**
 * Input data for the Remove Reaction use case.
 */
public class RemoveReactionInputData {

    private final String messageId;
    private final String userId;

    public RemoveReactionInputData(String messageId, String userId) {
        this.messageId = messageId;
        this.userId = userId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getUserId() {
        return userId;
    }
}
