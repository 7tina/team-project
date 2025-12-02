package usecase.messaging.send_m;

import entity.Chat;
import entity.Message;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

public class SendMessageInteractor implements SendMessageInputBoundary {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SendMessageOutputBoundary presenter;
    private final SendMessageDataAccessInterface dataAccess;

    public SendMessageInteractor(ChatRepository chatRepository,
                                 MessageRepository messageRepository,
                                 UserRepository userRepository,
                                 SendMessageOutputBoundary presenter,
                                 SendMessageDataAccessInterface dataAccess) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.presenter = presenter;
        this.dataAccess = dataAccess;
    }

    @Override
    public void execute(SendMessageInputData inputData) {
        String chatId = inputData.getChatId();
        String senderId = inputData.getSenderUserId();
        String repliedMessageId = inputData.getRepliedMessageId();
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
                repliedMessageId,
                content,
                Instant.now()
        );

        Message saved = dataAccess.sendMessage(message);
        dataAccess.updateChat(chatId, message.getId());

        // Array index order: [messageId, senderUserId, messageContent, messageTimestamp, repliedId]
        String senderName = senderOpt.get().getName();
        String[] msg = {
                saved.getId(),
                senderName,
                saved.getContent(),
                makeString(saved.getTimestamp()),
                saved.getRepliedMessageId()};

        SendMessageOutputData outputData =
                new SendMessageOutputData(chatId, msg);

        presenter.prepareSuccessView(outputData);
    }

    /**
     * Helper: timestamp
     */
    private String makeString(Instant timestamp) {
        ZoneId zone = ZoneId.of("UTC"); // Specify the desired time zone
        ZonedDateTime zdt = timestamp.atZone(zone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return zdt.format(formatter);
    }
}
