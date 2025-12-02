package usecase.messaging.remove_reaction;

import java.util.Map;

/**
 * Output data for the Remove Reaction use case.
 */
public class RemoveReactionOutputData {

    private final String messageId;
    private final Map<String, String> reactions;

    public RemoveReactionOutputData(String messageId, Map<String, String> reactions) {
        this.messageId = messageId;
        this.reactions = reactions;
    }

    public String getMessageId() {
        return messageId;
    }

    public Map<String, String> getReactions() {
        return reactions;
    }
}
