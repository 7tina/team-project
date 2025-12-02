package usecase.create_chat;

import java.util.List;

public class CreateChatInputData {
    private final String currentUserId;
    private final List<String> participantUsernames;
    private final String groupName;

    public CreateChatInputData(String currentUserId,
                               List<String> participantUsernames, String groupName) {
        this.currentUserId = currentUserId;
        this.participantUsernames = participantUsernames;
        this.groupName = groupName;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public List<String> getParticipantUsernames() {
        return participantUsernames;
    }

    public String getGroupName() {
        return groupName;
    }
}
