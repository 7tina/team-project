package use_case.messaging;

import entity.Message;
import entity.ports.MessageRepository;

import java.util.Optional;

public class DeleteMessageInteractor {

    private final MessageRepository messageRepository;

    public DeleteMessageInteractor(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void execute(String messageId, String requestingUserId) {
        Optional<Message> maybeMessage = messageRepository.findById(messageId);

        if (maybeMessage.isEmpty()) {
            return;
        }

        Message message = maybeMessage.get();

        if (!message.getSenderUserId().equals(requestingUserId)) {
            return;
        }

        messageRepository.deleteById(messageId);
    }
}
