package usecase.messaging.sendmessage;

/**
 * Output data for the send message use case.
 * <p>
 * This object is created by the interactor and passed to the presenter.
 */
public class SendMessageOutputData {

    private final String chatId;
    /**
     * Encoded message data.
     * <p>
     * Expected order:
     * <ol>
     *     <li>messageId</li>
     *     <li>senderDisplayName</li>
     *     <li>messageContent</li>
     *     <li>formattedTimestamp</li>
     *     <li>repliedMessageId</li>
     * </ol>
     */
    private final String[] message;

    /**
     * Constructs a {@code SendMessageOutputData} instance.
     *
     * @param chatId  the ID of the chat where the message was sent
     * @param message the encoded message data array
     */
    public SendMessageOutputData(String chatId, String[] message) {
        this.chatId = chatId;
        this.message = message;
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
     * Returns the encoded message data.
     *
     * @return the encoded message data
     */
    public String[] getMessage() {
        return message;
    }
}
