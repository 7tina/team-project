package usecase.messaging.remove_reaction;

/**
 * Input boundary for the Remove Reaction use case.
 */
public interface RemoveReactionInputBoundary {

    /**
     * Executes the remove reaction use case.
     *
     * @param inputData the input data containing message ID and user ID
     */
    void execute(RemoveReactionInputData inputData);
}