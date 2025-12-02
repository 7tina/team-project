package usecase.messaging.add_reaction;

/**
 * Output boundary for the Add Reaction use case.
 */
public interface AddReactionOutputBoundary {

    /**
     * Prepares the success view after adding a reaction.
     *
     * @param outputData the output data
     */
    void prepareSuccessView(AddReactionOutputData outputData);

    /**
     * Prepares the failure view if adding a reaction fails.
     *
     * @param errorMessage the error message
     */
    void prepareFailView(String errorMessage);
}
