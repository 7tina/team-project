package interfaceadapter.messaging.view_history;

import usecase.messaging.view_history.ViewChatHistoryInputBoundary;
import usecase.messaging.view_history.ViewChatHistoryInputData;

import java.util.List;

public class ViewChatHistoryController {

    private final ViewChatHistoryInputBoundary viewChatHistoryInteractor;

    public ViewChatHistoryController(ViewChatHistoryInputBoundary viewChatHistoryInteractor) {
        this.viewChatHistoryInteractor = viewChatHistoryInteractor;
    }

    public void execute(String chatId, List<String> userIds, List<String> messageIds) {
        ViewChatHistoryInputData inputData = new ViewChatHistoryInputData(chatId,  userIds, messageIds);
        viewChatHistoryInteractor.execute(inputData);
    }
}
