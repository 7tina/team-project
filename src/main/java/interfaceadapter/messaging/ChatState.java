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
    private Map<String, Map<String, String>> messageReactions = new HashMap<>();

    public ChatState() {}

    public boolean getFirst() {return first;}

    public void chatViewStart() {first=true;}

    public void chatViewStop() {first=false;}

    public boolean getIsGroup() {return isGroup;}

    public void setIsGroup(boolean isGroup) {this.isGroup = isGroup;}

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<String> getParticipants() {return participants;}

    public void addParticipant(String participant) {this.participants.add(participant);}

    public void removeParticipant(String participant) {this.participants.remove(participant);}

    public List<String> getMessageIds() {return messageIds;}

    public void addMessageId(String messageId) {messageIds.add(messageId);}

    public void clearMessageIds() {
        messageIds.clear();
    }

    public List<String[]> getMessages() {return messages;}

    public void addMessage(String[] message) {messages.add(message);}

    public void clearMessages() {
        messages.clear();
    }

    public Map<String, Map<String, String>> getMessageToReaction() {return messageToReaction;}

    public void addReaction(String messageId, String userId, String reaction) {
        // Update messageToReaction
        Map<String, String> reactions = messageToReaction.get(messageId);
        if (reactions == null) {
            reactions = new HashMap<>();
        }
        reactions.put(userId, reaction);
        messageToReaction.put(messageId, reactions);

        // CRITICAL: Also update messageReactions so getMessageReactions() works
        if (this.messageReactions == null) {
            this.messageReactions = new HashMap<>();
        }
        if (!this.messageReactions.containsKey(messageId)) {
            this.messageReactions.put(messageId, new HashMap<>());
        }
        this.messageReactions.get(messageId).put(userId, reaction);
    }

    public void removeReaction(String messageId, String userId, String reaction) {
        Map<String, String> reactions = messageToReaction.get(messageId);
        if (reactions != null && reactions.containsKey(userId) && reactions.get(userId).equals(reaction)) {
            reactions.remove(userId, reaction);
        }
        messageToReaction.put(messageId, reactions);
    }

    /**
     * Updates the reactions for a specific message.
     *
     * @param messageId the message ID
     * @param reactions the updated reactions map
     */
    public void updateMessageReaction(String messageId, Map<String, String> reactions) {
        // Store reactions in a map for quick lookup during rendering
        if (this.messageReactions == null) {
            this.messageReactions = new HashMap<>();
        }
        this.messageReactions.put(messageId, new HashMap<>(reactions));
    }

    /**
     * Gets the reactions for a specific message.
     *
     * @param messageId the message ID
     * @return map of userId -> emoji, or empty map if no reactions
     */
    public Map<String, String> getMessageReactions(String messageId) {
        if (messageReactions == null) {
            return new HashMap<>();
        }
        return messageReactions.getOrDefault(messageId, new HashMap<>());
    }

    public void clearReactions() {
        messageToReaction.clear();
        messageReactions.clear();  // ← ADD THIS LINE
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

    public void setError(String error) {
        this.error = error;
    }

    public void setMessages(List<String[]> newMessages) { this.messages.clear(); this.messages.addAll(newMessages); }

    public void setMessageIds(List<String> newMessageIds) {
        this.messageIds.clear();
        this.messageIds.addAll(newMessageIds);
    }
}
