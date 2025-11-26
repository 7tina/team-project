package use_case.create_chat;

import java.util.List;

public class CreateChatInputData {
    private final String currentUserId;
    private final List<String> participantUsernames;
    private final String groupName;
    private boolean isGroupChat;

    public CreateChatInputData(String currentUserId,
                               List<String> participantUsernames,  String groupName, boolean isGroupChat) {
        this.currentUserId = currentUserId;
        this.participantUsernames = participantUsernames;
        this.groupName = groupName;
        this.isGroupChat = isGroupChat;
    }

    public String getCurrentUserId() {return currentUserId;}

    public List<String> getParticipantUsernames() {return participantUsernames;}

    public String getGroupName() {return groupName;}

    public boolean isGroupChat() {return isGroupChat;}

}
