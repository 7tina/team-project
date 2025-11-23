package interface_adapter.messaging.send_m;

import use_case.messaging.send_m.SendMessageInputBoundary;
import use_case.messaging.send_m.SendMessageInputData;

public class SendMessageController {
    private final SendMessageInputBoundary interactor;

    public SendMessageController(SendMessageInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String chatId, String senderUserId, String repliedMessageId, String content) {
        SendMessageInputData inputData = new SendMessageInputData(chatId, senderUserId, repliedMessageId, content);
        interactor.execute(inputData);
    }
}
