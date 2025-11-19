package interface_adapter.messaging.view_history;

import use_case.messaging.view_history.ViewChatHistoryInputBoundary;
import use_case.messaging.view_history.ViewChatHistoryInputData;

public class ViewChatHistoryController {

    private final ViewChatHistoryInputBoundary viewChatHistoryInteractor;

    public ViewChatHistoryController(ViewChatHistoryInputBoundary viewChatHistoryInteractor) {
        this.viewChatHistoryInteractor = viewChatHistoryInteractor;
    }

    public void execute(String chatId) {
        ViewChatHistoryInputData inputData = new ViewChatHistoryInputData(chatId);
        viewChatHistoryInteractor.execute(inputData);
    }
}
