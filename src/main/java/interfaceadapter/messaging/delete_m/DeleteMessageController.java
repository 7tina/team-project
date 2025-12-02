package interfaceadapter.messaging.delete_m;

import usecase.messaging.delete_m.DeleteMessageInputBoundary;
import usecase.messaging.delete_m.DeleteMessageInputData;

public class DeleteMessageController {

    private final DeleteMessageInputBoundary interactor;

    public DeleteMessageController(DeleteMessageInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Deletes a message by delegating the operation to the interactor.
     * @param messageId the ID of the message to be deleted
     * @param currentUserId the ID of the user requesting the deletion
     */
    public void execute(String messageId, String currentUserId) {
        final DeleteMessageInputData input =
                new DeleteMessageInputData(messageId, currentUserId);
        interactor.execute(input);
    }
}
