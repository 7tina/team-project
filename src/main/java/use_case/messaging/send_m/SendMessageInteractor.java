package use_case.messaging.send_m;

import entity.Chat;
import entity.Message;
import entity.User;
import use_case.messaging.ChatMessageDto;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class SendMessageInteractor implements SendMessageInputBoundary {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SendMessageOutputBoundary presenter;

    public SendMessageInteractor(ChatRepository chatRepository,
                                 MessageRepository messageRepository,
                                 UserRepository userRepository,
                                 SendMessageOutputBoundary presenter) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.presenter = presenter;
    }

    @Override
    public void execute(SendMessageInputData inputData) {
        String chatId = inputData.getChatId();
        String senderId = inputData.getSenderUserId();
        String content = inputData.getContent();

        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            presenter.prepareFailView("Chat not found: " + chatId);
            return;
        }

        Optional<User> senderOpt = userRepository.findByUsername(senderId);
        if (senderOpt.isEmpty()) {
            presenter.prepareFailView("Sender not found: " + senderId);
            return;
        }

        Message message = new Message(
                UUID.randomUUID().toString(),
                chatId,
                senderId,
                content,
                Instant.now()
        );

        Message saved = messageRepository.save(message);

        String senderName = senderOpt.get().getName();
        ChatMessageDto dto = new ChatMessageDto(
                saved.getId(),
                saved.getSenderUserId(),
                senderName,
                saved.getContent(),
                saved.getTimestamp()
        );

        SendMessageOutputData outputData =
                new SendMessageOutputData(chatId, dto);

        presenter.prepareSuccessView(outputData);
    }
}
