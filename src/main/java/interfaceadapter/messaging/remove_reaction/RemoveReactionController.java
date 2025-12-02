package interfaceadapter.messaging.remove_reaction;

import usecase.messaging.remove_reaction.RemoveReactionInputBoundary;
import usecase.messaging.remove_reaction.RemoveReactionInputData;

/**
 * Controller for the Remove Reaction use case.
 */
public class RemoveReactionController {

    private final RemoveReactionInputBoundary interactor;

    public RemoveReactionController(RemoveReactionInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the remove reaction use case.
     *
     * @param messageId the ID of the message to remove reaction from
     * @param userId the ID of the user removing the reaction
     */
    public void execute(String messageId, String userId) {
        final RemoveReactionInputData inputData = new RemoveReactionInputData(
                messageId,
                userId
        );
        interactor.execute(inputData);
    }
}
