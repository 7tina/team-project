package interface_adapter.groupchat;

import java.util.ArrayList;
import java.util.List;

/**
 * State for the Group Chat view.
 */
public class GroupChatState {
    private String chatId;
    private String groupName;
    private List<String> participants;
    private List<String> messageIds;
    private boolean success;
    private String error;

    public GroupChatState() {
        this.participants = new ArrayList<>();
        this.messageIds = new ArrayList<>();
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    // ADDED: getParticipants method
    public List<String> getParticipants() {
        return participants;
    }

    // ADDED: setParticipants method
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    // ADDED: getMessageIds method
    public List<String> getMessageIds() {
        return messageIds;
    }

    // ADDED: setMessageIds method
    public void setMessageIds(List<String> messageIds) {
        this.messageIds = messageIds;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}