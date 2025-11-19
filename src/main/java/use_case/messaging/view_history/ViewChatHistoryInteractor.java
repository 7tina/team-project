package use_case.messaging.view_history;

import entity.Chat;
import entity.Message;
import goc.chat.entity.User;
import use_case.messaging.ChatMessageDto;
import use_case.ports.ChatRepository;
import use_case.ports.MessageRepository;
import use_case.ports.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Use case: view the history of a given chat.
 */
public class ViewChatHistoryInteractor implements ViewChatHistoryInputBoundary {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ViewChatHistoryOutputBoundary presenter;

    public ViewChatHistoryInteractor(ChatRepository chatRepository,
                                     MessageRepository messageRepository,
                                     UserRepository userRepository,
                                     ViewChatHistoryOutputBoundary presenter) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.presenter = presenter;
    }

    /**
     * Load all messages for the given chat and send them to the presenter,
     * sorted from oldest to newest.
     */
    public void execute(ViewChatHistoryInputData inputData) {
        String chatId = inputData.getChatId();

        // 1. confirm the existence of chat
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            presenter.prepareFailView("Chat not found: " + chatId);
            return;
        }

        // 2. extract all the message
        List<Message> messages = messageRepository.findByChatId(chatId);

        // 3. time sort
        messages.sort(Comparator.comparing(Message::getTimestamp));

        if (messages.isEmpty()) {
            presenter.prepareNoMessagesView(chatId);
            return;
        }

        // 4. change into list
        List<ChatMessageDto> dtos = new ArrayList<>();
        for (Message m : messages) {
            String senderName = resolveSenderName(m.getSenderUserId());
            dtos.add(new ChatMessageDto(
                    m.getId(),
                    m.getSenderUserId(),
                    senderName,
                    m.getContent(),
                    m.getTimestamp()
            ));
        }

        ViewChatHistoryOutputData outputData =
                new ViewChatHistoryOutputData(chatId, dtos);

        presenter.prepareSuccessView(outputData);
    }

    /**
     * Helper: username
     */
    private String resolveSenderName(String senderUserId) {
        Optional<User> userOpt = userRepository.findById(senderUserId);
        return userOpt.map(User::getUsername).orElse("Unknown");
    }
}

