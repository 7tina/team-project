package interfaceadapter.messaging.sendmessage;

import usecase.messaging.sendmessage.SendMessageInputBoundary;
import usecase.messaging.sendmessage.SendMessageInputData;

/**
 * Controller for the send message use case.
 * <p>
 * It receives raw input from the View (IDs and content), constructs a
 * {@link SendMessageInputData} object, and calls the interactor.
 */
public class SendMessageController {

    private final SendMessageInputBoundary interactor;

    /**
     * Constructs a {@code SendMessageController}.
     *
     * @param interactor input boundary for the send message use case
     */
    public SendMessageController(SendMessageInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the send message use case with the given parameters.
     *
     * @param chatId           ID of the chat to send the message in
     * @param senderUserId     ID of the user sending the message
     * @param repliedMessageId ID of the message being replied to, or {@code null}
     *                         if this is not a reply
     * @param content          message content
     */
    public void execute(String chatId,
                        String senderUserId,
                        String repliedMessageId,
                        String content) {
        SendMessageInputData inputData =
                new SendMessageInputData(chatId, senderUserId, repliedMessageId, content);
        interactor.execute(inputData);
    }
}
