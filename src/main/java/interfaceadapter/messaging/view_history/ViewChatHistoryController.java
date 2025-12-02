package interfaceadapter.messaging.view_history;

import java.util.List;

import usecase.messaging.view_history.ViewChatHistoryInputBoundary;
import usecase.messaging.view_history.ViewChatHistoryInputData;

public class ViewChatHistoryController {

    private final ViewChatHistoryInputBoundary viewChatHistoryInteractor;

    public ViewChatHistoryController(ViewChatHistoryInputBoundary viewChatHistoryInteractor) {
        this.viewChatHistoryInteractor = viewChatHistoryInteractor;
    }

    /**
     * Retrieves the chat history for a specified chat by delegating the operation to the interactor.
     *
     * @param chatId the ID of the chat whose history is to be retrieved
     * @param userIds a list of user IDs involved in the chat
     * @param messageIds a list of message IDs to include in the history
     */
    public void execute(String chatId, List<String> userIds, List<String> messageIds) {
        final ViewChatHistoryInputData inputData = new ViewChatHistoryInputData(chatId, userIds, messageIds);
        viewChatHistoryInteractor.execute(inputData);
    }
}
