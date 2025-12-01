package entity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a message in a chat.
 */
public class Message {

    private final String id;
    private final String chatId;
    private final String senderUserId;
    private final String repliedMessageId;
    /**
     * Reactions to this message, mapping user ID to emoji.
     */
    private final Map<String, String> reactions = new HashMap<>();
    private String content;
    private final Instant timestamp;

    /**
     * Constructs a new Message.
     *
     * @param id              the message ID
     * @param chatId          the ID of the chat this message belongs to
     * @param senderUserId    the ID of the user who sent the message
     * @param repliedMessageId the ID of the message this one replies to (may be null)
     * @param content         the text content of the message
     * @param timestamp       the time the message was sent
     */
    public Message(String id, String chatId, String senderUserId,
                   String repliedMessageId, String content, Instant timestamp) {
        this.id = id;
        this.chatId = chatId;
        this.senderUserId = senderUserId;
        this.repliedMessageId = repliedMessageId;
        this.content = content;
        this.timestamp = timestamp;
    }

    /**
     * Returns the message ID.
     *
     * @return the message ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the chat ID.
     *
     * @return the chat ID
     */
    public String getChatId() {
        return chatId;
    }

    /**
     * Returns the sender's user ID.
     *
     * @return the sender user ID
     */
    public String getSenderUserId() {
        return senderUserId;
    }

    /**
     * Returns the ID of the message this one replies to.
     *
     * @return the replied message ID
     */
    public String getRepliedMessageId() {
        return repliedMessageId;
    }

    /**
     * Returns the reactions on this message.
     *
     * @return a map from user ID to emoji
     */
    public Map<String, String> getReactions() {
        return reactions;
    }

    /**
     * Returns the content of this message.
     *
     * @return the message content
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the timestamp of this message.
     *
     * @return the message timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Adds or updates a reaction from a user.
     *
     * @param userId   the user ID
     * @param reaction the emoji reaction
     */
    public void addReaction(String userId, String reaction) {
        reactions.put(userId, reaction);
    }
}

