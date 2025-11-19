package use_case.messaging.search_history;

import entity.Chat;
import entity.Message;
import goc.chat.entity.User;
import use_case.messaging.ChatMessageDto;
import use_case.ports.ChatRepository;
import use_case.ports.MessageRepository;
import use_case.ports.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SearchChatHistoryInteractor implements SearchChatHistoryInputBoundary {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SearchChatHistoryOutputBoundary presenter;

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

        // 1. chat 是否存在
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            presenter.prepareFailView("Chat not found: " + chatId);
            return;
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            presenter.prepareFailView("Search keyword must not be empty.");
            return;
        }
        String normalized = keyword.toLowerCase();

        // 2. 拿到所有消息并按时间排序
        List<Message> messages = messageRepository.findByChatId(chatId);
        messages.sort(Comparator.comparing(Message::getTimestamp));

        // 3. 过滤出包含关键字的
        List<ChatMessageDto> dtos = new ArrayList<>();
        for (Message m : messages) {
            String content = m.getContent();
            if (content == null ||
                    !content.toLowerCase().contains(normalized)) {
                continue;
            }
            String senderName = resolveSenderName(m.getSenderUserId());
            dtos.add(new ChatMessageDto(
                    m.getId(),
                    m.getSenderUserId(),
                    senderName,
                    m.getContent(),
                    m.getTimestamp()
            ));
        }

        if (dtos.isEmpty()) {
            presenter.prepareNoMatchesView(chatId, keyword);
            return;
        }

        SearchChatHistoryOutputData outputData =
                new SearchChatHistoryOutputData(chatId, keyword, dtos);
        presenter.prepareSuccessView(outputData);
    }

    private String resolveSenderName(String senderUserId) {
        Optional<User> userOpt = userRepository.findById(senderUserId);
        return userOpt.map(User::getUsername).orElse("Unknown");
    }
}
