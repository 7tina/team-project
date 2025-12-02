package interfaceadapter.messaging.deletemessage;

import usecase.messaging.deletemessage.DeleteMessageInputBoundary;
import usecase.messaging.deletemessage.DeleteMessageInputData;

/**
 * Controller for the delete message use case.
 * <p>
 * It receives raw inputs from the View, constructs an
 * {@link DeleteMessageInputData} object, and calls the interactor.
 */
public class DeleteMessageController {

    private final DeleteMessageInputBoundary interactor;

    /**
     * Constructs a {@code DeleteMessageController}.
     *
     * @param interactor input boundary for the delete message use case
     */
    public DeleteMessageController(DeleteMessageInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the delete message use case with the given IDs.
     *
     * @param messageId     ID of the message to delete
     * @param currentUserId ID of the user requesting deletion
     */
    public void execute(String messageId, String currentUserId) {
        DeleteMessageInputData input =
                new DeleteMessageInputData(messageId, currentUserId);
        interactor.execute(input);
    }
}