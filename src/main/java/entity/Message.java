package entity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Message {
    private final String id;
    private final String chatId;
    private final String senderUserId;
    private final String repliedMessageId;
    private final Map<String, String> reactions = new HashMap<>(); //Key is userId and value is emoji reaction
    private String content;
    private final Instant timestamp;

    public Message(String id, String chatId, String senderUserId,
                   String repliedMessageId, String content, Instant timestamp) {
        this.id = id;
        this.chatId = chatId;
        this.senderUserId = senderUserId;
        this.repliedMessageId = repliedMessageId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }

    public String getChatId() { return chatId; }

    public String getSenderUserId() { return senderUserId; }

    public String getRepliedMessageId() { return repliedMessageId; }

    public Map<String, String> getReactions() { return reactions; }

    public String getContent() { return content; }

    public Instant getTimestamp() { return timestamp; }

    public void addReaction(String userId, String reaction) { reactions.put(userId, reaction); }
}
