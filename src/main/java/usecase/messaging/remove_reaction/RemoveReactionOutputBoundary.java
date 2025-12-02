package usecase.messaging.remove_reaction;

/**
 * Output boundary for the Remove Reaction use case.
 */
public interface RemoveReactionOutputBoundary {

    /**
     * Prepares the success view after removing a reaction.
     *
     * @param outputData the output data
     */
    void prepareSuccessView(RemoveReactionOutputData outputData);

    /**
     * Prepares the failure view if removing a reaction fails.
     *
     * @param errorMessage the error message
     */
    void prepareFailView(String errorMessage);
}
