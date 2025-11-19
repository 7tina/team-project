package interface_adapter.messaging.delete_m;

import use_case.messaging.delete_m.DeleteMessageInputBoundary;
import use_case.messaging.delete_m.DeleteMessageInputData;

public class DeleteMessageController {

    private final DeleteMessageInputBoundary interactor;

    public DeleteMessageController(DeleteMessageInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void deleteMessage(String messageId, String userId) {
        DeleteMessageInputData input = new DeleteMessageInputData(messageId, userId);
        interactor.delete(input);
    }
}
