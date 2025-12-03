package usecase.messaging.view_history;

import java.util.List;

/**
 * Data access interface for the View Chat History use case.
 * Defines how chat messages are retrieved from storage.
 */
public interface ViewChatHistoryDataAccessInterface {

    /**
     * Retrieves the messages associated with a given chat.
     *
     * @param chatId      the ID of the chat whose history is being viewed
     * @param userIds     an output list that will be filled with the sender user IDs
     * @param messageIds  an output list that will be filled with the message IDs
     */
    void findChatMessages(String chatId, List<String> userIds, List<String> messageIds);
}
