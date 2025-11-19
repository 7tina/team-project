package use_case.messaging.delete_m;

import use_case.ports.MessageRepository;
import entity.Message;

public class DeleteMessageInteractor implements DeleteMessageInputBoundary {

    private final MessageRepository messageRepository;
    private final DeleteMessageOutputBoundary presenter;

    public DeleteMessageInteractor(MessageRepository messageRepository,
                                   DeleteMessageOutputBoundary presenter) {
        this.messageRepository = messageRepository;
        this.presenter = presenter;
    }


    @Override
    public void delete(DeleteMessageInputData inputData) {
        Message message = messageRepository.findById(inputData.getMessageId());

        if (message == null) {
            presenter.prepareFailView("Message not found.");
            return;
        }

        // only sender can delete the message
        if (!message.getSenderUserId().equals(inputData.getRequestingUserId())) {
            presenter.prepareFailView("You do not have permission to delete this message.");
            return;
        }

        messageRepository.deleteById(message.getId());

        presenter.prepareSuccessView(
                new DeleteMessageOutputData(message.getId())
        );
    }
}
