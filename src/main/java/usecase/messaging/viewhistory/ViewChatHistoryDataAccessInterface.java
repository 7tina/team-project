package usecase.messaging.viewhistory;

import java.util.List;

/**
 * Data access interface for the View Chat History use case.
 * Provides methods to retrieve messages for a given chat and filters.
 */
public interface ViewChatHistoryDataAccessInterface {

    /**
     * Finds messages for the given chat that match the provided filters.
     *
     * @param chatId     the id of the chat whose history is being viewed
     * @param userIds    the list of user ids whose messages should be included
     * @param messageIds the list of message ids to retrieve
     */
    void findChatMessages(String chatId, List<String> userIds, List<String> messageIds);
}
