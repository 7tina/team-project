package interface_adapter.messaging.add_reaction;

import use_case.messaging.add_reaction.AddReactionInputBoundary;
import use_case.messaging.add_reaction.AddReactionInputData;

/**
 * Controller for the Add Reaction use case.
 */
public class AddReactionController {

    private final AddReactionInputBoundary interactor;

    public AddReactionController(AddReactionInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the add reaction use case.
     *
     * @param messageId the ID of the message to react to
     * @param userId the ID of the user adding the reaction
     * @param emoji the emoji reaction
     */
    public void execute(String messageId, String userId, String emoji) {
        final AddReactionInputData inputData = new AddReactionInputData(
                messageId,
                userId,
                emoji
        );
        interactor.execute(inputData);
    }
}
