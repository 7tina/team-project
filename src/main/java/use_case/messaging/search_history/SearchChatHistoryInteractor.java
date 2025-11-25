package use_case.messaging.search_history;

import entity.Chat;
import entity.Message;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SearchChatHistoryInteractor implements SearchChatHistoryInputBoundary {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SearchChatHistoryOutputBoundary presenter;

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ISO_INSTANT;

    public SearchChatHistoryInteractor(ChatRepository chatRepository,
                                       MessageRepository messageRepository,
                                       UserRepository userRepository,
                                       SearchChatHistoryOutputBoundary presenter) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.presenter = presenter;
    }

    @Override
    public void execute(SearchChatHistoryInputData inputData) {
        String chatId = inputData.getChatId();
        String keyword = inputData.getKeyword();

        // 1. Verify chat existence
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            presenter.prepareFailView("Chat not found: " + chatId);
            return;
        }

        // 2. Validate keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            presenter.prepareFailView("Search keyword must not be empty.");
            return;
        }
        String normalized = keyword.toLowerCase();

        // 3. Retrieve and sort messages
        List<Message> messages = messageRepository.findByChatId(chatId);
        messages.sort(Comparator.comparing(Message::getTimestamp));

        // 4. Filter by keyword and convert to String[]
        List<String[]> matching = new ArrayList<>();
        for (Message m : messages) {
            String content = m.getContent();
            if (content == null || !content.toLowerCase().contains(normalized)) {
                continue;
            }
            matching.add(toStringArray(m));
        }

        // 5. Send result to presenter
        if (matching.isEmpty()) {
            presenter.prepareNoMatchesView(chatId, keyword);
        } else {
            SearchChatHistoryOutputData output =
                    new SearchChatHistoryOutputData(chatId, keyword, matching);
            presenter.prepareSuccessView(output);
        }
    }

    private String[] toStringArray(Message m) {
        String messageId = m.getId();
        String senderId = m.getSenderUserId();
        String senderName = resolveSenderName(senderId);
        String content = m.getContent();
        String time = m.getTimestamp() == null
                ? ""
                : TIME_FORMATTER.format(m.getTimestamp());

        // Match the same format used by ViewChatHistory:
        // [id, senderId, senderName, content, time]
        return new String[]{messageId, senderId, senderName, content, time};
    }

    private String resolveSenderName(String senderUserId) {
        // In your domain, "user id" is effectively the username,
        // and UserRepository looks up by username.
        Optional<User> userOpt = userRepository.findByUsername(senderUserId);
        return userOpt.map(User::getName).orElse("Unknown");
    }
}
