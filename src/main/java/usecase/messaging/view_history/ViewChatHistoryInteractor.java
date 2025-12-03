package usecase.messaging.view_history;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Message;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;

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
            final String chatId = inputData.getChatId();
            final List<String> userIds = inputData.getUserIds();
            final List<String> messageIds = inputData.getMessageIds();

            dataAccess.findChatMessages(chatId, userIds, messageIds);

            final List<Message> messageList = messageRepository.findByChatId(chatId);
            messageList.sort(Comparator.comparing(Message::getTimestamp));

            final List<String[]> messagesData = new ArrayList<>();
            final Map<String, Map<String, String>> reactions = new HashMap<>();

            for (Message msg : messageList) {
                final String[] data = new String[5];
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

            final ViewChatHistoryOutputData outputData = new ViewChatHistoryOutputData(
                    messagesData,
                    reactions
            );

            presenter.prepareSuccessView(outputData);

        }
        catch (Exception e) {
            presenter.prepareFailView("Failed to load chat history: " + e.getMessage());
        }
    }

    /**
     * Converts an {@link Instant} timestamp into a formatted date-time string.
     *
     * <p>
     * The timestamp is converted to the UTC time zone and formatted as
     * "dd-MM-yyyy HH:mm:ss".
     *
     * </p>
     *
     * @param timestamp the {@link Instant} to be formatted
     * @return a string representation of the timestamp in UTC, formatted
     *         as "dd-MM-yyyy HH:mm:ss"
     */
    private String makeString(Instant timestamp) {
        // Specify the desired time zone
        final ZoneId zone = ZoneId.of("UTC");
        final ZonedDateTime zdt = timestamp.atZone(zone);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return zdt.format(formatter);
    }
}

