package use_case.messaging.search_history;

import entity.Chat;
import entity.Message;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for searching messages by keyword in a given chat.
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

        if (keyword == null || keyword.trim().isEmpty()) {
            presenter.prepareFailView("Search keyword must not be empty.");
            return;
        }

        if (chatRepository.findById(chatId).isEmpty()) {
            presenter.prepareFailView("Chat not found: " + chatId);
            return;
        }

        List<Message> allMessages = messageRepository.findByChatId(chatId);
        List<Message> matching = new ArrayList<>();

        String keywordLower = keyword.toLowerCase();
        for (Message message : allMessages) {
            String content = message.getContent();
            if (content != null && content.toLowerCase().contains(keywordLower)) {
                matching.add(message);
            }
        }

        if (matching.isEmpty()) {
            presenter.prepareNoMatchesView(chatId, keyword);
        } else {
            SearchChatHistoryOutputData outputData = new SearchChatHistoryOutputData(matching);
            presenter.prepareSuccessView(outputData);
        }
    }
}
