package usecase.messaging.remove_reaction;

import dataaccess.FireBaseUserDataAccessObject;
import entity.Message;
import entity.ports.MessageRepository;

import java.util.Optional;

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
            return;
        }

        final Message message = messageOpt.get();

        // Remove reaction from the message
        message.getReactions().remove(userId);

        // Save to repository
        messageRepository.save(message);

        // Save to Firebase
        try {
            dataAccess.removeReactionFromMessage(messageId, userId);

            // Prepare success view
            final RemoveReactionOutputData outputData = new RemoveReactionOutputData(
                    messageId,
                    message.getReactions()
            );
            outputBoundary.prepareSuccessView(outputData);

        } catch (Exception e) {
            outputBoundary.prepareFailView("Failed to remove reaction: " + e.getMessage());
        }
    }
}