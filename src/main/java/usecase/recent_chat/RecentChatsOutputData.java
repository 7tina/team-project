package usecase.recent_chat;

import java.util.HashMap;
import java.util.List;

public class RecentChatsOutputData {
    private final List<String> chatNames;
    private final HashMap<String, String> nameToChatIds;

    public RecentChatsOutputData(List<String> chatNames, HashMap<String, String> nameToChatIds) {
        this.chatNames = chatNames;
        this.nameToChatIds = nameToChatIds;
    }
    public List<String> getChatNames() {
        return chatNames;
    }

    public HashMap<String, String> getNameToChatIds() {
        return nameToChatIds;
    }
}
