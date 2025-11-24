package use_case.groups;

import java.util.List;

/**
 * Output Data for the Create Group Chat Use Case.
 */
public class CreateGroupChatOutputData {
    private final String chatId;
    private final String groupName;
    private final List<String> participantUserIds;
    private final boolean success;
    private final String errorMessage;

    /**
     * Constructor for successful group chat creation.
     */
    public CreateGroupChatOutputData(String chatId, String groupName,
                                     List<String> participantUserIds,
                                     boolean success, String errorMessage) {
        this.chatId = chatId;
        this.groupName = groupName;
        this.participantUserIds = participantUserIds;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public String getChatId() {
        return chatId;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<String> getParticipantUserIds() {
        return participantUserIds;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}