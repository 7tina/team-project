package usecase.messaging.searchhistory;

public class SearchChatHistoryInputData {
    private final String chatId;
    private final String keyword;

    public SearchChatHistoryInputData(String chatId, String keyword) {
        this.chatId = chatId;
        this.keyword = keyword;
    }

    public String getChatId() {
        return chatId;
    }

    public String getKeyword() {
        return keyword;
    }
}
