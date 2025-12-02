package interfaceadapter.messaging.delete_m;

import usecase.messaging.delete_m.DeleteMessageInputBoundary;
import usecase.messaging.delete_m.DeleteMessageInputData;

public class DeleteMessageController {

    private final DeleteMessageInputBoundary interactor;

    public DeleteMessageController(DeleteMessageInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String messageId, String currentUserId) {
        DeleteMessageInputData input =
                new DeleteMessageInputData(messageId, currentUserId);
        interactor.execute(input);
    }
}