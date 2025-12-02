package use_case.messaging.add_reaction;

/**
 * Input boundary for the Add Reaction use case.
 */
public interface AddReactionInputBoundary {

    /**
     * Executes the add reaction use case.
     *
     * @param inputData the input data containing message ID, user ID, and emoji
     */
    void execute(AddReactionInputData inputData);
}
