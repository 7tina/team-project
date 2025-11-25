package use_case.messaging.search_history;

import entity.Chat;
import entity.Message;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Use case: search messages in a chat by keyword.
 */
public class SearchChatHistoryInteractor implements SearchChatHistoryInputBoundary {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final SearchChatHistoryOutputBoundary presenter;

    public SearchChatHistoryInteractor(ChatRepository chatRepository,
                                       MessageRepository messageRepository,
                                       SearchChatHistoryOutputBoundary presenter) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.presenter = presenter;
    }

    @Override
    public void execute(SearchChatHistoryInputData inputData) {
        String chatId = inputData.getChatId();
        String keyword = inputData.getKeyword();

        // 1. Validate chatId.
        if (chatId == null || chatId.isEmpty()) {
            presenter.prepareFailView("Chat id must not be empty.");
            return;
        }

        // 2. Check that the chat exists.
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            presenter.prepareFailView("Chat not found: " + chatId);
            return;
        }

        // 3. Validate keyword.
        if (keyword == null || keyword.isEmpty()) {
            presenter.prepareFailView("Search keyword must not be empty.");
            return;
        }

        // 4. Retrieve all messages for this chat.
        List<Message> allMessages = messageRepository.findByChatId(chatId);

        // 5. Filter by keyword (case-insensitive).
        String keywordLower = keyword.toLowerCase();
        List<Message> matching = new ArrayList<>();
        for (Message message : allMessages) {
            String content = message.getContent();
            if (content != null && content.toLowerCase().contains(keywordLower)) {
                matching.add(message);
            }
        }

        // 6. Present results.
        if (matching.isEmpty()) {
            presenter.prepareNoMatchesView(chatId, keyword);
        } else {
            SearchChatHistoryOutputData outputData =
                    new SearchChatHistoryOutputData(matching);
            presenter.prepareSuccessView(outputData);
        }
    }
}
