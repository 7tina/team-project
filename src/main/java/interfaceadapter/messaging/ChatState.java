package interfaceadapter.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The state object stored inside ChatViewModel.
 */
public class ChatState {

    private boolean first;
    private boolean isGroup;
    private String chatId;
    private final List<String> participants = new ArrayList<>();
    private final List<String> messageIds = new ArrayList<>();
    private final List<String[]> messages = new ArrayList<>();
    // Key: messageId, Value: nested key: userId, nested value: reaction
    private final Map<String, Map<String, String>> messageToReaction = new HashMap<>();
    private String groupName;
    private boolean success;
    private String error;

    public ChatState() {

    }

    public boolean getFirst() {
        return first;
    }

    /**
     * Marks that the chat view has started.
     */
    public void chatViewStart() {
        first = true;
    }

    /**
     * Marks that the chat view has stopped.
     */
    public void chatViewStop() {
        first = false;
    }

    public boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<String> getParticipants() {
        return participants;
    }

    /**
     * Adds a participant to the chat.
     * @param participant the username of the participant to add
     */
    public void addParticipant(String participant) {
        this.participants.add(participant);
    }

    /**
     * Removes a participant from the chat.
     * @param participant the username of the participant to remove
     */
    public void removeParticipant(String participant) {
        this.participants.remove(participant);
    }

    /**
     * Returns the list of message IDs in the chat.
     * @return a list of message IDs
     */
    public List<String> getMessageIds() {
        return messageIds;
    }

    /**
     * Adds a message ID to the chat.
     * @param messageId the ID of the message to add
     */
    public void addMessageId(String messageId) {
        messageIds.add(messageId);
    }

    /**
     * Clears all message IDs from the chat state.
     */
    public void clearMessageIds() {
        messageIds.clear();
    }

    /**
     * Returns the list of messages in the chat.
     * @return a list of messages, where each message is represented as a String array
     */
    public List<String[]> getMessages() {
        return messages;
    }

    /**
     * Adds a message to the chat.
     * @param message the message to add, represented as a String array
     */
    public void addMessage(String[] message) {
        messages.add(message);
    }

    /**
     * Clears all messages from the chat state.
     */
    public void clearMessages() {
        messages.clear();
    }

    public Map<String, Map<String, String>> getMessageToReaction() {
        return messageToReaction;
    }

    /**
     * Adds a reaction to a specific message from a specific user.
     * @param messageId the ID of the message to react to
     * @param userId the ID of the user adding the reaction
     * @param reaction the reaction to add
     */
    public void addReaction(String messageId, String userId, String reaction) {
        final Map<String, String> reactions = messageToReaction.get(messageId);
        if (reactions != null) {
            reactions.put(userId, reaction);
        }
        messageToReaction.put(messageId, reactions);
    }

    /**
     * Removes a reaction from a specific message for a specific user.
     * @param messageId the ID of the message
     * @param userId the ID of the user
     * @param reaction the reaction to remove
     */
    public void removeReaction(String messageId, String userId, String reaction) {
        final Map<String, String> reactions = messageToReaction.get(messageId);
        if (reactions != null && reactions.containsKey(userId) && reactions.get(userId).equals(reaction)) {
            reactions.remove(userId, reaction);
        }
        messageToReaction.put(messageId, reactions);
    }

    /**
     * Clears all reactions from all messages in the chat state.
     */
    public void clearReactions() {
        messageToReaction.clear();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    /**
     * Sets the error message for the last operation.
     * @param error the error message to set
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Replaces all messages in the chat state with a new list of messages.
     * @param newMessages the new list of messages
     */
    public void setMessages(List<String[]> newMessages) {
        this.messages.clear();
        this.messages.addAll(newMessages);
    }

    /**
     * Replaces all message IDs in the chat state with a new list of IDs.
     * @param newMessageIds the new list of message IDs
     */
    public void setMessageIds(List<String> newMessageIds) {
        this.messageIds.clear();
        this.messageIds.addAll(newMessageIds);
    }
}
