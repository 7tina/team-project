package entity;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Chat {
    private final String id;
    private final List<String> participantUserIds = new ArrayList<>();
    private final List<String> messageIds = new ArrayList<>();
    private String groupName;
    private Color backgroundColor;
    private Instant lastMessage;

    public Chat(String id, String groupName, Color backgroundColor, Instant lastMessage) {
        this.id = id;
        this.groupName = groupName;
        this.backgroundColor = backgroundColor;
        this.lastMessage = lastMessage;
    }

    public String getId() { return id; }

    public List<String> getParticipantUserIds() { return participantUserIds; }

    public List<String> getMessageIds() { return messageIds; }

    public String getGroupName() { return this.groupName;}

    public Color getBackgroundColor() { return this.backgroundColor; }

    public Instant getLastMessage() { return this.lastMessage; }

    public void addParticipant(String userId) { participantUserIds.add(userId); }

    public void addMessage(String messageId) { messageIds.add(messageId); }

    public void setGroupName(String groupName) {this.groupName = groupName; }

    public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }

    public void setLastMessage(Instant lastMessage) { this.lastMessage = lastMessage; }
}