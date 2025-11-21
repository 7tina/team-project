package use_case.messaging.send_m;

import data_access.FireBaseUserDataAccessObject;
import entity.Chat;
import entity.Message;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import use_case.messaging.ChatMessageDto;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class SendMessageInteractor implements SendMessageInputBoundary {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final FireBaseUserDataAccessObject userDao;
    private final SendMessageOutputBoundary presenter;

    public SendMessageInteractor(ChatRepository chatRepository,
                                 MessageRepository messageRepository,
                                 FireBaseUserDataAccessObject userDao,
                                 SendMessageOutputBoundary presenter) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userDao = userDao;
        this.presenter = presenter;
    }

    @Override
    public void execute(SendMessageInputData inputData) {

        // chatId picked
        String chatId = inputData.getChatId();

        // sender
        String senderId = userDao.getCurrentUsername();

        if (senderId == null) {
            presenter.prepareFailView("No user logged in.");
            return;
        }

        String content = inputData.getContent();

        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            presenter.prepareFailView("Chat not found: " + chatId);
            return;
        }

        User senderUser = userDao.get(senderId);
        if (senderUser == null) {
            presenter.prepareFailView("Sender not found in database: " + senderId);
            return;
        }

        Message message = new Message(
                UUID.randomUUID().toString(),
                chatId,
                senderId,
                content,
                Instant.now()
        );

        // update
        messageRepository.save(message);

        ChatMessageDto dto = new ChatMessageDto(
                message.getId(),
                message.getSenderUserId(),
                senderUser.getName(),
                message.getContent(),
                message.getTimestamp()
        );

        SendMessageOutputData outputData =
                new SendMessageOutputData(chatId, dto);

        presenter.prepareSuccessView(outputData);
    }
}
