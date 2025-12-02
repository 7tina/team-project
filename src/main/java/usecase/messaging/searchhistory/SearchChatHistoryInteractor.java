package usecase.messaging.searchhistory;

import java.util.ArrayList;
import java.util.List;

import entity.Message;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;

/**
 * Interactor for searching messages by keyword in a given chat.
 * Implements the SearchChatHistoryInputBoundary interface.
 */
public class SearchChatHistoryInteractor implements SearchChatHistoryInputBoundary {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final SearchChatHistoryOutputBoundary presenter;

    /**
     * Constructs a SearchChatHistoryInteractor.
     *
     * @param chatRepository    repository for chat data
     * @param messageRepository repository for message data
     * @param presenter         presenter to handle output data
     */
    public SearchChatHistoryInteractor(final ChatRepository chatRepository,
                                       final MessageRepository messageRepository,
                                       final SearchChatHistoryOutputBoundary presenter) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.presenter = presenter;
    }

    /**
     * Executes the use case of searching chat history by keyword.
     *
     * @param inputData input data containing chatId and search keyword
     */
    @Override
    public void execute(final SearchChatHistoryInputData inputData) {
        final String chatId = inputData.getChatId();
        final String keyword = inputData.getKeyword();

        if (keyword == null || keyword.trim().isEmpty()) {
            presenter.prepareFailView("Search keyword must not be empty.");
        }
        else if (chatRepository.findById(chatId).isEmpty()) {
            presenter.prepareFailView("Chat not found: " + chatId);
        }
        else {
            final List<Message> allMessages = messageRepository.findByChatId(chatId);
            final List<Message> matching = new ArrayList<>();

            final String keywordLower = keyword.toLowerCase();
            for (Message message : allMessages) {
                final String content = message.getContent();
                if (content != null && content.toLowerCase().contains(keywordLower)) {
                    matching.add(message);
                }
            }

            if (matching.isEmpty()) {
                presenter.prepareNoMatchesView(chatId, keyword);
            }
            else {
                final SearchChatHistoryOutputData outputData =
                        new SearchChatHistoryOutputData(matching);
                presenter.prepareSuccessView(outputData);
            }
        }
    }
}
