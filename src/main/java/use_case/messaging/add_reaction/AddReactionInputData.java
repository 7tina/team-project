package use_case.messaging.add_reaction;

/**
 * Input data for the Add Reaction use case.
 */
public class AddReactionInputData {

    private final String messageId;
    private final String userId;
    private final String emoji;

    public AddReactionInputData(String messageId, String userId, String emoji) {
        this.messageId = messageId;
        this.userId = userId;
        this.emoji = emoji;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmoji() {
        return emoji;
    }
}