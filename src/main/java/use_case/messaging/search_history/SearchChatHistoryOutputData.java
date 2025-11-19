package use_case.messaging.search_history;

import use_case.messaging.ChatMessageDto;
import java.util.List;

public class SearchChatHistoryOutputData {
    private final String chatId;
    private final String keyword;
    private final List<ChatMessageDto> messages;

    public SearchChatHistoryOutputData(String chatId,
                                       String keyword,
                                       List<ChatMessageDto> messages) {
        this.chatId = chatId;
        this.keyword = keyword;
        this.messages = messages;
    }

    public String getChatId() { return chatId; }
    public String getKeyword() { return keyword; }
    public List<ChatMessageDto> getMessages() { return messages; }
}
