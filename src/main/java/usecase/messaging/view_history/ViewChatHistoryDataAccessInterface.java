package usecase.messaging.view_history;

import java.util.List;

public interface ViewChatHistoryDataAccessInterface {

    /**
     * Retrieves the message history for the specified chat and populates
     * the provided lists with the associated user IDs and message IDs.
     * @param chatId      the unique identifier of the chat whose history
     *                    is being retrieved
     * @param userIds     an output list that will be filled with the IDs
     *                    of the users who sent the messages
     * @param messageIds  an output list that will be filled with the IDs
     *                    of the messages in the chat
     */
    void findChatMessages(String chatId, List<String> userIds, List<String> messageIds);
}
