package use_case.messaging.send_m;

/**
 * Input boundary for the send message use case.
 * <p>
 * This interface is called by the controller layer to trigger the use case.
 */
public interface SendMessageInputBoundary {

    /**
     * Executes the send message use case with the given input data.
     *
     * @param inputData input data including chat ID, sender information,
     *                  optional replied message ID, and content
     */
    void execute(SendMessageInputData inputData);
}
