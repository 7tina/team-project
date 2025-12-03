package usecase.messaging.view_history;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use case: view the history of a given chat.
 */
public class ViewChatHistoryInteractor implements ViewChatHistoryInputBoundary {

    private static final int MESSAGE_DATA_SIZE = 5;
    private static final int INDEX_ID = 0;
    private static final int INDEX_SENDER = 1;
    private static final int INDEX_CONTENT = 2;
    private static final int INDEX_TIMESTAMP = 3;
    private static final int INDEX_REPLY_TO = 4;

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
        final String chatId = inputData.getChatId();
        final List<String> userIds = inputData.getUserIds();
        final List<String> messageIds = inputData.getMessageIds();

        try {
            // 1) Chat 是否存在
            if (chatRepository.findById(chatId).isEmpty()) {
                presenter.prepareFailView("Chat not found: " + chatId);
                return;
            }

            // 2) 让 dataAccess 做它需要的预加载 / 校验工作
            dataAccess.findChatMessages(chatId, userIds, messageIds);

            // 3) 从仓库拿到所有消息
            final List<Message> messageList =
                    new ArrayList<>(messageRepository.findByChatId(chatId));

            if (messageList.isEmpty()) {
                presenter.prepareFailView("No messages in this chat: " + chatId);
                return;
            }

            // 4) 按时间从旧到新排序
            messageList.sort(Comparator.comparing(Message::getTimestamp));

            // 5) 组装给 presenter 的数据
            final List<String[]> messagesData = new ArrayList<>();
            final Map<String, Map<String, String>> reactions = new HashMap<>();

            for (Message msg : messageList) {
                final String[] data = new String[MESSAGE_DATA_SIZE];
                data[INDEX_ID] = msg.getId();
                data[INDEX_SENDER] = msg.getSenderUserId();
                data[INDEX_CONTENT] = msg.getContent();
                data[INDEX_TIMESTAMP] = makeString(msg.getTimestamp());
                data[INDEX_REPLY_TO] =
                        msg.getRepliedMessageId() != null ? msg.getRepliedMessageId() : "";

                messagesData.add(data);

                if (msg.getReactions() != null && !msg.getReactions().isEmpty()) {
                    reactions.put(msg.getId(), new HashMap<>(msg.getReactions()));
                }
            }

            final ViewChatHistoryOutputData outputData =
                    new ViewChatHistoryOutputData(messagesData, reactions);

            presenter.prepareSuccessView(outputData);
        } catch (Exception e) {
            presenter.prepareFailView("Failed to load chat history: " + e.getMessage());
        }
    }

    /**
     * Converts an {@link Instant} timestamp into a formatted date-time string.
     *
     * <p>The timestamp is converted to the UTC time zone and formatted as
     * {@code "dd-MM-yyyy HH:mm:ss"}.</p>
     *
     * @param timestamp the {@link Instant} to be formatted
     * @return a string representation of the timestamp in UTC, formatted as
     *         {@code "dd-MM-yyyy HH:mm:ss"}
     */
    private String makeString(final Instant timestamp) {
        final ZoneId zone = ZoneId.of("UTC");
        final ZonedDateTime zdt = timestamp.atZone(zone);
        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return zdt.format(formatter);
    }
}
