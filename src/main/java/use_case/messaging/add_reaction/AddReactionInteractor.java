package use_case.messaging.add_reaction;

import data_access.FireBaseUserDataAccessObject;
import entity.Message;
import entity.ports.MessageRepository;

import java.util.Optional;

/**
 * Interactor for the Add Reaction use case.
 */
public class AddReactionInteractor implements AddReactionInputBoundary {

    private final MessageRepository messageRepository;
    private final FireBaseUserDataAccessObject dataAccess;
    private final AddReactionOutputBoundary outputBoundary;

    public AddReactionInteractor(MessageRepository messageRepository,
                                 FireBaseUserDataAccessObject dataAccess,
                                 AddReactionOutputBoundary outputBoundary) {
        this.messageRepository = messageRepository;
        this.dataAccess = dataAccess;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(AddReactionInputData inputData) {
        final String messageId = inputData.getMessageId();
        final String userId = inputData.getUserId();
        final String emoji = inputData.getEmoji();

        // Find the message
        final Optional<Message> messageOpt = messageRepository.findById(messageId);

        if (messageOpt.isEmpty()) {
            outputBoundary.prepareFailView("Message not found.");
            return;
        }

        final Message message = messageOpt.get();

        // Add reaction to the message
        message.addReaction(userId, emoji);

        // Save to repository
        messageRepository.save(message);

        // Save to Firebase
        try {
            dataAccess.addReactionToMessage(messageId, userId, emoji);

            // Prepare success view
            final AddReactionOutputData outputData = new AddReactionOutputData(
                    messageId,
                    message.getReactions()
            );
            outputBoundary.prepareSuccessView(outputData);

        } catch (Exception e) {
            outputBoundary.prepareFailView("Failed to save reaction: " + e.getMessage());
        }
    }
}
