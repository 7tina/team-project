package interface_adapter.messaging.delete_m;

import use_case.messaging.delete_m.DeleteMessageInputBoundary;
import use_case.messaging.delete_m.DeleteMessageInputData;

/**
 * Controller for the Delete Message use case.
 * Takes user input (message ID) from the View and executes the Interactor.
 */
public class DeleteMessageController {

    private final DeleteMessageInputBoundary deleteMessageInteractor;

    public DeleteMessageController(DeleteMessageInputBoundary deleteMessageInteractor) {
        this.deleteMessageInteractor = deleteMessageInteractor;
    }

    public void execute(String messageId, String currentUserId) {
        DeleteMessageInputData inputData = new DeleteMessageInputData(messageId, currentUserId);
        deleteMessageInteractor.execute(inputData);
    }
}