package interfaceadapter.messaging.send_m;

import usecase.messaging.send_m.SendMessageInputBoundary;
import usecase.messaging.send_m.SendMessageInputData;

public class SendMessageController {
    private final SendMessageInputBoundary interactor;

    public SendMessageController(SendMessageInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Sends a message in a specified chat by delegating the operation to the interactor.
     * @param chatId the ID of the chat where the message will be sent
     * @param senderUserId the ID of the user sending the message
     * @param repliedMessageId the ID of the message being replied to, or null if not a reply
     * @param content the content of the message to send
     */
    public void execute(String chatId, String senderUserId, String repliedMessageId, String content) {
        final SendMessageInputData inputData =
                new SendMessageInputData(chatId, senderUserId, repliedMessageId, content);
        interactor.execute(inputData);
    }
}
