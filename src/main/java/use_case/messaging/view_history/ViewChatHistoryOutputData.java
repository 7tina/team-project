package use_case.messaging.view_history;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewChatHistoryOutputData {
    private final List<String[]> messages;
    private final Map<String, Map<String, String>> reactions;

    public ViewChatHistoryOutputData(List<String[]> messages,
                                     Map<String, Map<String, String>> reactions) {
        this.messages = messages;
        this.reactions = reactions;
    }

    public List<String[]> getMessages() {
        return messages;
    }

    public Map<String, Map<String, String>> getReactions() {
        return reactions;
    }
}