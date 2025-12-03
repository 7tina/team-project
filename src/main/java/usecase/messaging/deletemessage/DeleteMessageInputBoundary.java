package usecase.messaging.deletemessage;

/**
 * Input boundary for the delete message use case.
 *
 * <p>
 * Called by the controller to initiate the use case.
 */
public interface DeleteMessageInputBoundary {

    /**
     * Executes the delete message use case.
     *
     * @param inputData data required to perform deletion
     */
    void execute(DeleteMessageInputData inputData);
}
