package interfaceadapter.messaging.send_m;

import usecase.messaging.send_m.SendMessageInputBoundary;
import usecase.messaging.send_m.SendMessageInputData;

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
