package use_case.messaging.search_history;

import entity.Chat;
import entity.Message;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

        // 1. 检查 chat 是否存在
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            presenter.prepareFailView("Chat not found: " + chatId);
            return;
        }

        // 2. 检查 keyword 合法性
        if (keyword == null || keyword.trim().isEmpty()) {
            presenter.prepareFailView("Search keyword must not be empty.");
            return;
        }

        String normalized = keyword.toLowerCase();

        // 3. 拿到 chat 的所有消息并按时间排序
        List<Message> messages = messageRepository.findByChatId(chatId);
        messages.sort(Comparator.comparing(Message::getTimestamp));

        // 4. 过滤出包含关键字的消息
        List<Message> matching = new ArrayList<>();
        for (Message m : messages) {
            String content = m.getContent();
            if (content != null && content.toLowerCase().contains(normalized)) {
                matching.add(m);
            }
        }

        // 5. 根据是否有匹配结果调用不同的 presenter 分支
        if (matching.isEmpty()) {
            presenter.prepareNoMatchesView(chatId, keyword);
        } else {
            SearchChatHistoryOutputData output =
                    new SearchChatHistoryOutputData(chatId, keyword, matching);
            presenter.prepareSuccessView(output);
        }
    }
}
