package entity;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chat entity containing participants, messages, and metadata.
 */
public class Chat {

    private final String id;
    private final List<String> participantUserIds = new ArrayList<>();
    private final List<String> messageIds = new ArrayList<>();
    private String groupName;
    private Color backgroundColor;
    private Instant lastMessage;

    /**
     * Constructs a new Chat.
     *
     * @param id               the chat ID
     * @param groupName        the name of the chat group
     * @param backgroundColor  the background color of the chat
     * @param lastMessage      the timestamp of the last message
     */
    public Chat(String id, String groupName, Color backgroundColor, Instant lastMessage) {
        this.id = id;
        this.groupName = groupName;
        this.backgroundColor = backgroundColor;
        this.lastMessage = lastMessage;
    }

    /**
     * Returns the chat ID.
     *
     * @return the chat ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the list of participant user IDs.
     *
     * @return list of participant user IDs
     */
    public List<String> getParticipantUserIds() {
        return participantUserIds;
    }

    /**
     * Returns the list of message IDs.
     *
     * @return list of message IDs
     */
    public List<String> getMessageIds() {
        return messageIds;
    }

    /**
     * Returns the chat group name.
     *
     * @return the group name
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Returns the background color of the chat.
     *
     * @return the background color
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns the timestamp of the last message.
     *
     * @return the last message timestamp
     */
    public Instant getLastMessage() {
        return lastMessage;
    }

    /**
     * Adds a participant to the chat.
     *
     * @param userId the participant's user ID
     */
    public void addParticipant(String userId) {
        participantUserIds.add(userId);
    }

    /**
     * Adds a message to the chat.
     *
     * @param messageId the message ID
     */
    public void addMessage(String messageId) {
        messageIds.add(messageId);
    }

    /**
     * Sets the group name for the chat.
     *
     * @param groupName the group name to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Sets the background color for the chat.
     *
     * @param backgroundColor the color to set
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Sets the timestamp of the last message.
     *
     * @param lastMessage the timestamp to set
     */
    public void setLastMessage(Instant lastMessage) {
        this.lastMessage = lastMessage;
    }

    /**
     * Removes a participant from the chat.
     *
     * @param userId the participant's user ID to remove
     */
    public void removeParticipant(String userId) {
        participantUserIds.remove(userId);
    }
}
