package usecase.messaging.sendmessage;

/**
 * Output boundary for the send message use case.
 *
 * <p>
 * Implementations are responsible for preparing data for the view layer
 * (e.g., updating a view model).
 */
public interface SendMessageOutputBoundary {

    /**
     * Prepares the success view when the message is sent successfully.
     *
     * @param outputData output data describing the sent message and chat context
     */
    void prepareSuccessView(SendMessageOutputData outputData);

    /**
     * Prepares the failure view when sending the message fails.
     *
     * @param errorMessage a description of the error to display
     */
    void prepareFailView(String errorMessage);
}
