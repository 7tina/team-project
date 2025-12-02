package usecase.messaging.viewhistory;

import java.util.List;

/**
 * Input data for viewing the chat history of a given chat.
 * Contains identifiers for the chat, users, and messages.
 */
public class ViewChatHistoryInputData {

    private final String chatId;
    private final List<String> userIds;
    private final List<String> messageIds;

    /**
     * Constructs a ViewChatHistoryInputData object.
     *
     * @param chatId     the ID of the chat whose history is being viewed
     * @param userIds    the list of user IDs to include in the view
     * @param messageIds the list of message IDs to include in the view
     */
    public ViewChatHistoryInputData(final String chatId,
                                    final List<String> userIds,
                                    final List<String> messageIds) {
        this.chatId = chatId;
        this.userIds = userIds;
        this.messageIds = messageIds;
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
     * Returns the list of user IDs.
     *
     * @return the list of user IDs
     */
    public List<String> getUserIds() {
        return userIds;
    }

    /**
     * Returns the list of message IDs.
     *
     * @return the list of message IDs
     */
    public List<String> getMessageIds() {
        return messageIds;
    }
}
