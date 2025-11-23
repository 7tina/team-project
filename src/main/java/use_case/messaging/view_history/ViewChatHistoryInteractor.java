package use_case.messaging.view_history;

import entity.Chat;
import entity.Message;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    private final ViewChatHistoryDataAccessInterface dataAccess;

    public ViewChatHistoryInteractor(ChatRepository chatRepository,
                                     MessageRepository messageRepository,
                                     UserRepository userRepository,
                                     ViewChatHistoryOutputBoundary presenter,
                                     ViewChatHistoryDataAccessInterface dataAccess) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.presenter = presenter;
        this.dataAccess = dataAccess;
    }

    /**
     * Load all messages for the given chat and send them to the presenter,
     * sorted from oldest to newest.
     */
    public void execute(ViewChatHistoryInputData inputData) {
        String chatId = inputData.getChatId();
        List<String> userIds = inputData.getUserIds();
        List<String> messageIds = inputData.getMessageIds();

        // 1. confirm the existence of chat
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            presenter.prepareFailView("Chat not found: " + chatId);
            return;
        }

        // 2. extract all the message
        dataAccess.findChatMessages(chatId, userIds, messageIds);
        List<Message> messages = messageRepository.findByChatId(chatId);

        // 3. time sort
        messages.sort(Comparator.comparing(Message::getTimestamp));

        if (messages.isEmpty()) {
            presenter.prepareNoMessagesView(chatId);
            return;
        }

        // 4. change into list
        // Array index order: [messageId, senderUserId, messageContent, messageTimestamp]
        List<String[]> msgs = new ArrayList<>();
        for (Message m : messages) {
            String[] msg = {m.getId(), m.getSenderUserId(), m.getContent(), makeString(m.getTimestamp())};
            msgs.add(msg);
        }

        ViewChatHistoryOutputData outputData =
                new ViewChatHistoryOutputData(chatId, msgs);

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

