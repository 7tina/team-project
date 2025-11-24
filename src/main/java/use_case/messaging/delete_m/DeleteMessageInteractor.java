package use_case.messaging.delete_m;

import entity.Message;
import entity.ports.MessageRepository;

import java.util.Optional;
import java.time.LocalDateTime;

public class DeleteMessageInteractor implements DeleteMessageInputBoundary {

    final MessageRepository messageRepository;
    final DeleteMessageOutputBoundary presenter;

    public DeleteMessageInteractor(MessageRepository messageRepository,
                                   DeleteMessageOutputBoundary presenter) {
        this.messageRepository = messageRepository;
        this.presenter = presenter;
    }

    @Override
    public void execute(DeleteMessageInputData inputData) {
        String messageId = inputData.getMessageId();
        String currentUserId = inputData.getCurrentUserId();

        // Lookup message using findById and handle Optional
        Optional<Message> optionalMessage = messageRepository.findById(messageId);

        if (optionalMessage.isEmpty()) {
            presenter.prepareFailView("Error: The message to be deleted could not be found.");
            return;
        }

        Message messageToDelete = optionalMessage.get();

        // Only sender can delete the message.
        if (!messageToDelete.getSenderID().equals(currentUserId)) {
            presenter.prepareFailView("Error: You do not have permission to delete this message. You can only delete messages that you have sent.");
            return;
        }

        // Execute the deletion using deleteById
        try {
            messageRepository.deleteById(messageId);

            // Prepare the success view.
            LocalDateTime now = LocalDateTime.now();
            DeleteMessageOutputData successOutput = new DeleteMessageOutputData(messageId, now);
            presenter.prepareSuccessView(successOutput);

        } catch (Exception e) {
            presenter.prepareFailView("Deletion failed due to a system error: " + e.getMessage());
        }
    }
}