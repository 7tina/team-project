package usecase.messaging.view_history;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import entity.Message;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;

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
    @Override
    public void execute(ViewChatHistoryInputData inputData) {
        try {
            String chatId = inputData.getChatId();
            List<String> userIds = inputData.getUserIds();
            List<String> messageIds = inputData.getMessageIds();

            dataAccess.findChatMessages(chatId, userIds, messageIds);

            List<Message> messageList = messageRepository.findByChatId(chatId);
            messageList.sort(Comparator.comparing(Message::getTimestamp));

            List<String[]> messagesData = new ArrayList<>();
            Map<String, Map<String, String>> reactions = new HashMap<>();

            for (Message msg : messageList) {
                String[] data = new String[5];
                data[0] = msg.getId();
                data[1] = msg.getSenderUserId();
                data[2] = msg.getContent();
                data[3] = makeString(msg.getTimestamp());
                data[4] = msg.getRepliedMessageId() != null ? msg.getRepliedMessageId() : "";

                messagesData.add(data);

                // Collect reactions
                if (msg.getReactions() != null && !msg.getReactions().isEmpty()) {
                    reactions.put(msg.getId(), new HashMap<>(msg.getReactions()));
                }
            }

            ViewChatHistoryOutputData outputData = new ViewChatHistoryOutputData(
                    messagesData,
                    reactions
            );

            presenter.prepareSuccessView(outputData);

        } catch (Exception e) {
            presenter.prepareFailView("Failed to load chat history: " + e.getMessage());
        }
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

