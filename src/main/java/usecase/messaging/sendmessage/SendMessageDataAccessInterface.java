package usecase.messaging.sendmessage;

import entity.Message;

/**
 * Data access interface for the send message use case.
 * <p>
 * Implementations are responsible for persisting messages and updating
 * any chat-related metadata when a new message is sent.
 */
public interface SendMessageDataAccessInterface {

    /**
     * Persists the given message and returns the saved instance.
     *
     * @param message the message to be saved
     * @return the saved message (may include additional persistence-related
     *         fields such as generated IDs)
     */
    Message sendMessage(Message message);

    /**
     * Updates the chat after a new message is sent.
     * <p>
     * For example, this might update the chat's list of message IDs or
     * the chat's last-updated timestamp.
     *
     * @param chatId    the chat that should be updated
     * @param messageId the ID of the newly-sent message
     */
    void updateChat(String chatId, String messageId);
}
