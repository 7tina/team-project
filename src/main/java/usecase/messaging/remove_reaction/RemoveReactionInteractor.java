package usecase.messaging.remove_reaction;

import java.util.Optional;

import dataaccess.FireBaseUserDataAccessObject;
import entity.Message;
import entity.ports.MessageRepository;

/**
 * Interactor for the Remove Reaction use case.
 */
public class RemoveReactionInteractor implements RemoveReactionInputBoundary {

    private final MessageRepository messageRepository;
    private final FireBaseUserDataAccessObject dataAccess;
    private final RemoveReactionOutputBoundary outputBoundary;

    public RemoveReactionInteractor(MessageRepository messageRepository,
                                    FireBaseUserDataAccessObject dataAccess,
                                    RemoveReactionOutputBoundary outputBoundary) {
        this.messageRepository = messageRepository;
        this.dataAccess = dataAccess;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(RemoveReactionInputData inputData) {
        final String messageId = inputData.getMessageId();
        final String userId = inputData.getUserId();

        // Find the message
        final Optional<Message> messageOpt = messageRepository.findById(messageId);

        if (messageOpt.isEmpty()) {
            outputBoundary.prepareFailView("Message not found.");
        }
        else {
            final Message message = messageOpt.get();

            message.getReactions().remove(userId);
            messageRepository.save(message);

            try {
                dataAccess.removeReactionFromMessage(messageId, userId);
                final RemoveReactionOutputData outputData =
                        new RemoveReactionOutputData(messageId, message.getReactions());
                outputBoundary.prepareSuccessView(outputData);
            }
            catch (Exception e) {
                outputBoundary.prepareFailView("Failed to remove reaction: " + e.getMessage());
            }
        }
    }
}
