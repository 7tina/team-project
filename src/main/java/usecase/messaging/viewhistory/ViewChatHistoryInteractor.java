package usecase.messaging.viewhistory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import entity.Chat;
import entity.Message;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;

/**
 * Use case for viewing the history of a given chat.
 */
public class ViewChatHistoryInteractor implements ViewChatHistoryInputBoundary {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ViewChatHistoryOutputBoundary presenter;
    private final ViewChatHistoryDataAccessInterface dataAccess;

    /**
     * Constructs a ViewChatHistoryInteractor.
     *
     * @param chatRepository     repository for chat data
     * @param messageRepository  repository for message data
     * @param userRepository     repository for user data
     * @param presenter          presenter that prepares the output data
     * @param dataAccess         data access interface for loading messages
     */
    public ViewChatHistoryInteractor(final ChatRepository chatRepository,
                                     final MessageRepository messageRepository,
                                     final UserRepository userRepository,
                                     final ViewChatHistoryOutputBoundary presenter,
                                     final ViewChatHistoryDataAccessInterface dataAccess) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.presenter = presenter;
        this.dataAccess = dataAccess;
    }

    /**
     * Loads all messages for the given chat and sends them to the presenter,
     * sorted from oldest to newest.
     *
     * @param inputData the input data containing the chat id and filters
     */
    @Override
    public void execute(final ViewChatHistoryInputData inputData) {
        final String chatId = inputData.getChatId();
        final List<String> userIds = inputData.getUserIds();
        final List<String> messageIds = inputData.getMessageIds();

        // 1. Confirm the existence of the chat
        final Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            presenter.prepareFailView("Chat not found: " + chatId);
        }
        else {
            // 2. Load all messages for this chat using the data access interface
            dataAccess.findChatMessages(chatId, userIds, messageIds);
            final List<Message> messages = messageRepository.findByChatId(chatId);

            // 3. Sort messages by timestamp
            messages.sort(Comparator.comparing(Message::getTimestamp));

            if (messages.isEmpty()) {
                presenter.prepareNoMessagesView(chatId);
            }
            else {
                // 4. Convert messages into a list of string arrays and reactions
                // Array index order: [messageId, senderUserId, messageContent, messageTimestamp, repliedId]
                final List<String[]> msgs = new ArrayList<>();
                final Map<String, Map<String, String>> msgToReactions = new HashMap<>();

                for (Message message : messages) {
                    final String[] msg = {
                            message.getId(),
                            message.getSenderUserId(),
                            message.getContent(),
                            makeString(message.getTimestamp()),
                            message.getRepliedMessageId(),
                    };
                    msgs.add(msg);
                }

                for (Message message : messages) {
                    final String msgId = message.getId();
                    final Map<String, String> reactions = message.getReactions();
                    msgToReactions.put(msgId, reactions);
                }

                final ViewChatHistoryOutputData outputData =
                        new ViewChatHistoryOutputData(chatId, msgs, msgToReactions);
                presenter.prepareSuccessView(outputData);
            }
        }
    }

    /**
     * Converts a timestamp to a formatted string.
     *
     * @param timestamp the timestamp to format
     * @return the formatted timestamp string
     */
    private String makeString(final Instant timestamp) {
        final ZoneId zone = ZoneId.of("UTC");
        final ZonedDateTime zonedDateTime = timestamp.atZone(zone);
        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return zonedDateTime.format(formatter);
    }
}
