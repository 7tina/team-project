package usecase.messaging.view_history;

import entity.Message;

import java.util.List;

public interface ViewChatHistoryDataAccessInterface {

    void findChatMessages(String chatId, List<String> userIds, List<String> messageIds);
}
