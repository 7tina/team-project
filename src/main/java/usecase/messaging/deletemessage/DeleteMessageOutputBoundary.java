package usecase.messaging.deletemessage;

/**
 * Output boundary for the delete message use case.
 *
 * <p>
 * A presenter implements this interface to prepare the view model.
 */
public interface DeleteMessageOutputBoundary {

    /**
     * Prepares the success view after the message is successfully deleted.
     *
     * @param outputData output data describing the deletion result
     */
    void prepareSuccessView(DeleteMessageOutputData outputData);

    /**
     * Prepares the failure view when message deletion fails.
     *
     * @param outputData output data containing failure details
     */
    void prepareFailView(DeleteMessageOutputData outputData);
}
