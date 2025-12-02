package usecase.messaging.add_reaction;

import java.util.Map;

/**
 * Output data for the Add Reaction use case.
 */
public class AddReactionOutputData {

    private final String messageId;
    private final Map<String, String> reactions;

    public AddReactionOutputData(String messageId, Map<String, String> reactions) {
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
