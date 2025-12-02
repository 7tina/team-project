package interfaceadapter.messaging.viewhistory;

import java.util.List;

import usecase.messaging.viewhistory.ViewChatHistoryInputBoundary;
import usecase.messaging.viewhistory.ViewChatHistoryInputData;

/**
 * Controller for the view chat history use case.
 */
public class ViewChatHistoryController {

    private final ViewChatHistoryInputBoundary viewChatHistoryInteractor;

    /**
     * Creates a ViewChatHistoryController with the given interactor.
     *
     * @param viewChatHistoryInteractor the interactor that handles the use case
     */
    public ViewChatHistoryController(ViewChatHistoryInputBoundary viewChatHistoryInteractor) {
        this.viewChatHistoryInteractor = viewChatHistoryInteractor;
    }

    /**
     * Executes the view chat history use case.
     *
     * @param chatId     the id of the chat whose history is requested
     * @param userIds    the ids of the users in the chat
     * @param messageIds the ids of the messages in the chat
     */
    public void execute(String chatId, List<String> userIds, List<String> messageIds) {
        final ViewChatHistoryInputData inputData =
                new ViewChatHistoryInputData(chatId, userIds, messageIds);
        viewChatHistoryInteractor.execute(inputData);
    }
}
