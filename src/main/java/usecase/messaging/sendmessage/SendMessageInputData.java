package usecase.messaging.sendmessage;

/**
 * Input data for the send message use case.
 *
 * <p>
 * This is created in the controller layer and passed into the interactor.
 */
public class SendMessageInputData {

    private final String chatId;
    private final String senderUserId;
    private final String repliedMessageId;
    private final String content;

    /**
     * Constructs a {@code SendMessageInputData} instance.
     *
     * @param chatId           the ID of the chat to which the message belongs
     * @param senderUserId     the ID (or username, depending on design) of the sender
     * @param repliedMessageId the ID of the message being replied to, or {@code null}
     *                         if this is not a reply
     * @param content          the content of the message
     */
    public SendMessageInputData(String chatId,
                                String senderUserId,
                                String repliedMessageId,
                                String content) {
        this.chatId = chatId;
        this.senderUserId = senderUserId;
        this.repliedMessageId = repliedMessageId;
        this.content = content;
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
     * Returns the sender user ID.
     *
     * @return the sender user ID
     */
    public String getSenderUserId() {
        return senderUserId;
    }

    /**
     * Returns the replied message ID.
     *
     * @return the replied message ID, or {@code null} if this is not a reply
     */
    public String getRepliedMessageId() {
        return repliedMessageId;
    }

    /**
     * Returns the message content.
     *
     * @return the message content
     */
    public String getContent() {
        return content;
    }
}
