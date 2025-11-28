package interface_adapter.messaging.delete_m;

import use_case.messaging.delete_m.DeleteMessageInputBoundary;
import use_case.messaging.delete_m.DeleteMessageInputData;

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